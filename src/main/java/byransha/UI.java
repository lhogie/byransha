package byransha;

import byransha.web.EndpointJsonResponse;
import byransha.web.NodeEndpoint;
import byransha.web.WebServer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sun.net.httpserver.HttpsExchange;

public class UI extends BNode {

    ColorNode backgroundColor;
    ColorNode textColor;

    public UI(BBGraph db, User creator) {
        super(db, creator);
        backgroundColor = new ColorNode(db, creator);
        backgroundColor.set("#A9A9A9", creator);
        textColor = new ColorNode(db, creator);
        textColor.set("#000000", creator);
    }

    public UI(BBGraph db, User creator, int id) {
        super(db, creator, id);
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
            UI ui = graph.find(UI.class, e -> true);
            r.set("bg_color", new TextNode(ui.backgroundColor.get()));
            r.set("text_color", new TextNode(ui.backgroundColor.get()));
            return new EndpointJsonResponse(r, this);
        }
    }
}
