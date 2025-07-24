package byransha.web.view;

import byransha.BBGraph;
import byransha.BNode;
import byransha.User;
import byransha.web.DevelopmentView;
import byransha.web.EndpointJsonResponse;
import byransha.web.EndpointResponse;
import byransha.web.NodeEndpoint;
import byransha.web.WebServer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sun.net.httpserver.HttpsExchange;

public class ColorNodeView
    extends NodeEndpoint<BNode>
    implements DevelopmentView {

    public ColorNodeView(BBGraph db) {
        super(db);
    }

    public ColorNodeView(BBGraph db, int id) {
        super(db, id);
    }

    @Override
    public boolean sendContentByDefault() {
        return true;
    }

    @Override
    public EndpointResponse exec(
        ObjectNode input,
        User user,
        WebServer webServer,
        HttpsExchange exchange,
        BNode node
    ) throws Throwable {
        if (
            node.color == null || node.color.getAsString() == null
        ) return new EndpointJsonResponse(
            new TextNode("default color"),
            "color"
        );
        return new EndpointJsonResponse(
            new TextNode(node.color.getAsString()),
            "color"
        );
    }

    @Override
    public String whatItDoes() {
        return "returns the color of a node";
    }
}
