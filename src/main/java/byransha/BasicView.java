package byransha;

import byransha.web.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sun.net.httpserver.HttpsExchange;

public class BasicView extends NodeEndpoint<BNode> implements View {

    @Override
    public String whatItDoes() {
        return "show basic info on node";
    }

    public BasicView(BBGraph g) {
        super(g);
    }

    @Override
    public EndpointResponse exec(
            ObjectNode in,
            User u,
            WebServer webServer,
            HttpsExchange exchange,
            BNode node
    ) throws Throwable {
        var n = new ObjectNode(null);
        n.set("class", new TextNode(node.getClass().getName()));
        n.set("id", new TextNode("" + node.id()));

        if (isPersisting()) {
            n.set("directory", new TextNode(directory().getAbsolutePath()));
        }

        n.set("out-degree", new TextNode("" + node.outDegree()));
        n.set(
                "outs",
                new TextNode(
                        node
                                .outs()
                                .entrySet()
                                .stream()
                                .map(e -> e.getKey() + "=" + e.getValue())
                                .toList()
                                .toString()
                )
        );
        //			n.set("in-degree", new TextNode("" + node.ins().size()));
        //			n.set("ins", new TextNode(node.ins().stream().map(e -> e.toString()).toList().toString()));
        n.set("canSee", new TextNode("" + node.canSee(u)));
        n.set("canEdit", new TextNode("" + node.canEdit(u)));
        return new EndpointJsonResponse(n, this);
    }

    @Override
    public boolean sendContentByDefault() {
        return true;
    }
}
