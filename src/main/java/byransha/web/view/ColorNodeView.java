package byransha.web.view;

import byransha.BNode;
import byransha.User;
import byransha.ValuedNode;
import byransha.web.*;
import byransha.BBGraph;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.web.EndpointJsonResponse.dialects;
import toools.src.Source;

public class ColorNodeView extends NodeEndpoint<BNode> implements DevelopmentView {

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
    public EndpointResponse exec(ObjectNode input, User user, WebServer webServer, HttpsExchange exchange, BNode node)
            throws Throwable {
        return new EndpointTextResponse("text/hex", pw -> {
            if (node instanceof ValuedNode) {
                ValuedNode vn = (ValuedNode) node;
                String color = vn.getColor();
                pw.print(color);
            } else {
                pw.print("No color available");
            }
        });
    }

    @Override
    public String whatItDoes() {
        return "ColorNodeView for BNode";
    }
}
