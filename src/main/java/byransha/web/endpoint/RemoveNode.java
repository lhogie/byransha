package byransha.web.endpoint;

import byransha.*;
import byransha.web.EndpointJsonResponse;
import byransha.web.NodeEndpoint;
import byransha.web.WebServer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sun.net.httpserver.HttpsExchange;
import java.util.concurrent.atomic.AtomicInteger;

public class RemoveNode extends NodeEndpoint<BNode> {

    public RemoveNode(BBGraph g) {
        super(g);
    }

    public RemoveNode(BBGraph g, int id) {
        super(g, id);
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
        if (
            !node.ins().isEmpty() ||
            node.getClass().getSimpleName().equals("graph")
        ) return new EndpointJsonResponse(
            new ObjectNode(null).set(
                "ins",
                new TextNode(node.ins().toString())
            ),
            "Node cannot be removed because it has incoming links or it is the graph."
        );

        var numberOfOuts = node.outs().size();
        AtomicInteger numberOfOutsDeleted = new AtomicInteger(0);
        node.forEachOut((n, outNode) -> {
            var b = new ObjectNode(null);
            b.set("outgoing link to", new TextNode(outNode.toString()));
            if (outNode instanceof ListNode<?> ls) ls.removeAll();
            else if (outNode instanceof ListNode<?> ss) ss.removeAll();
            if (!outNode.getClass().getSimpleName().equals("graph")) {
                outNode.remove();
                numberOfOutsDeleted.getAndIncrement();
            }
            a.add(b);
        });
        if (numberOfOutsDeleted.get() == numberOfOuts) node.remove();
        return new EndpointJsonResponse(a, "Node removed from the graph.");
    }

    @Override
    public String whatItDoes() {
        return "Remove a node from the graph.";
    }
}
