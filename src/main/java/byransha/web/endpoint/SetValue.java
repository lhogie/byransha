package byransha.web.endpoint;

import byransha.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.web.EndpointJsonResponse;
import byransha.web.NodeEndpoint;
import byransha.web.WebServer;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Iterator;
import java.util.Map;

public class SetValue extends NodeEndpoint<BNode> {

	@Override
	public String whatItDoes() {
		return "modify the value of valued nodes";
	}

	public SetValue(BBGraph g) {
		super(g);
	}

	public SetValue(BBGraph g, int id) {
		super(g, id);
	}

	@Override
	public EndpointJsonResponse exec(ObjectNode in, User user, WebServer webServer, HttpsExchange exchange,
			BNode target) throws Throwable {

		var a = new ObjectNode(null);

        if (!in.isEmpty()) {
            int id = in.get("id").asInt();
            var node = graph.findByID(id);
            a.set("id", new IntNode(node.id()));
            a.set("name", new TextNode(node.prettyName()));
            a.set("type", new TextNode(node.getClass().getSimpleName()));

            var value = in.get("value");

            if (node instanceof StringNode sn) {
                sn.set(value.asText());
                a.set("value", new TextNode(value.asText()));
            } else if (node instanceof byransha.IntNode i) {
                i.set(value.asInt());
                a.set("value", new IntNode(value.asInt()));
            } else if (node instanceof ImageNode im) {
                String base64Image = value.asText();
                byte[] data = Base64.getDecoder().decode(base64Image);
                im.set(data);
                a.set("value", new TextNode(im.get().toString()));
            }

            in.removeAll();
        }

		return new EndpointJsonResponse(a, "Setting the value");
	}
}
