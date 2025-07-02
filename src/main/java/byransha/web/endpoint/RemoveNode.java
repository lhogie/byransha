package byransha.web.endpoint;

import byransha.BBGraph;
import byransha.BNode;
import byransha.User;
import byransha.web.EndpointJsonResponse;
import byransha.web.NodeEndpoint;
import byransha.web.WebServer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sun.net.httpserver.HttpsExchange;

public class RemoveNode extends NodeEndpoint<BNode> {

    public RemoveNode(BBGraph g) {
        super(g);
    }

    public RemoveNode(BBGraph g, int id) {
        super(g, id);
    }

    @Override
    public EndpointJsonResponse exec(ObjectNode input, User user, WebServer webServer, HttpsExchange exchange, BNode node) throws Throwable {
        var a = new ArrayNode(null);
        node.forEachIn((n, inNode) -> {
            var b = new ObjectNode(null);
            b.set("incoming link from", new TextNode(inNode.toString()));
            a.add(b);
        });

        node.forEachOut((n, outNode) -> {
            var b = new ObjectNode(null);
            b.set("outgoing link to", new TextNode(outNode.toString()));
            if(!outNode.getClass().getSimpleName().equals("graph")) outNode.remove();
            a.add(b);
        });

        if(!node.getClass().getSimpleName().equals("graph")) node.remove();
        return new EndpointJsonResponse(a, "Node removed from the graph.");
    }

    @Override
    public String whatItDoes() {
        return "Remove a node from the graph.";
    }
}

