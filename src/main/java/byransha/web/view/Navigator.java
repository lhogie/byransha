package byransha.web.view;

import byransha.BBGraph;
import byransha.nodes.BNode;
import byransha.nodes.system.User;
import byransha.web.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sun.net.httpserver.HttpsExchange;

public class Navigator extends NodeEndpoint<BNode> implements TechnicalView {

    @Override
    public String whatItDoes() {
        return "navigates the graph";
    }

    public Navigator(BBGraph g) {
        super(g);
    }

    @Override
    public boolean sendContentByDefault() {
        return true;
    }

    @Override
    public EndpointResponse exec(
            ObjectNode in,
            User u,
            WebServer webServer,
            HttpsExchange exchange,
            BNode n
    ) {
        var r = new ObjectNode(null);
        var outs = new ObjectNode(null);
        n.forEachOutField((name, o) ->
                outs.set(name, new TextNode("" + o.id()))
        );
        r.set("outs", outs);
        var ins = new ObjectNode(null);
        n.forEachIn((name, o) -> ins.set(name, new TextNode("" + o.id())));
        r.set("ins", ins);
        return new EndpointJsonResponse(r, "bnode_nav2");
    }
}
