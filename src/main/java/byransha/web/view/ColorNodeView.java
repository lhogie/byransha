package byransha.web.view;

import byransha.BBGraph;
import byransha.BNode;
import byransha.User;
import byransha.web.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sun.net.httpserver.HttpsExchange;

public class ColorNodeView
    extends NodeEndpoint<BNode>
    implements TechnicalView {

    public ColorNodeView(BBGraph db) {
        super(db);
        endOfConstructor();
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
