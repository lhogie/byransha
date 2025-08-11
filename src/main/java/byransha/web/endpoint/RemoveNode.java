package byransha.web.endpoint;

import byransha.*;
import byransha.web.EndpointJsonResponse;
import byransha.web.NodeEndpoint;
import byransha.web.WebServer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sun.net.httpserver.HttpsExchange;
import java.util.concurrent.atomic.AtomicInteger;

public class RemoveNode extends NodeEndpoint<BNode> {

    public RemoveNode(BBGraph g) {
        super(g);
    }

    @Override
    public EndpointJsonResponse exec(
        ObjectNode input,
        User user,
        WebServer webServer,
        HttpsExchange exchange,
        BNode node
    ) throws Throwable {
        var a = new ArrayNode(null);
        if (node instanceof BBGraph) return new EndpointJsonResponse(
            new ObjectNode(null).set(
                "id",
                new IntNode(node.id())
            ),
            "Node cannot be removed because it is the graph."
        );
        boolean delete = requireParm(input, "delete").asBoolean();
        if(delete){
            graph.deleteNode(node);
            return new EndpointJsonResponse(a, "Node removed from the graph.");
        }
        else{
            for (BNode allDeleteNode : graph.getAllDeleteNodes(node)) {
                var b = new ObjectNode(null);
                b.set("id", new IntNode(allDeleteNode.id()));
                b.set("class", new TextNode(allDeleteNode.getClass().getSimpleName()));
                a.add(b);
            }
            return new EndpointJsonResponse(a, "All those node will be affected.");
        }
    }

    @Override
    public String whatItDoes() {
        return "Remove a node from the graph.";
    }
}
