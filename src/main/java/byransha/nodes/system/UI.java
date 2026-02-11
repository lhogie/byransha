package byransha.nodes.system;

import byransha.BBGraph;
import byransha.nodes.BNode;
import byransha.nodes.primitive.ColorNode;
import byransha.web.EndpointJsonResponse;
import byransha.web.NodeEndpoint;
import byransha.web.WebServer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sun.net.httpserver.HttpsExchange;

public class UI extends BNode {

    ColorNode backgroundColor;
    ColorNode textColor;

    public UI(BBGraph g, User creator) {
        super(g, creator);
        backgroundColor = new ColorNode(g, creator);
        backgroundColor.set("#A9A9A9", creator);
        textColor = new ColorNode(g, creator);
        textColor.set("#000000", creator);
    }


    @Override
    public String prettyName() {
        return "UI preferences";
    }

    @Override
    public String whatIsThis() {
        return "UI preferences";
    }

    public static class getProperties extends NodeEndpoint<BNode> {

        public getProperties(BBGraph db) {
            super(db);
        }


        @Override
        public String whatItDoes() {
            return "gets UI properties for the frontend";
        }

        @Override
        public EndpointJsonResponse exec(
            ObjectNode inputJson,
            User user,
            WebServer webServer,
            HttpsExchange exchange,
            BNode node
        ) {
            var r = new ObjectNode(null);
            UI ui = g.find(UI.class, e -> true);
            r.set("bg_color", new TextNode(ui.backgroundColor.get()));
            r.set("text_color", new TextNode(ui.backgroundColor.get()));
            return new EndpointJsonResponse(r, this);
        }
    }
}
