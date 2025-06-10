package byransha.web.endpoint;

import byransha.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.web.EndpointJsonResponse;
import byransha.web.NodeEndpoint;
import byransha.web.WebServer;

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

		if(!in.isEmpty()) {
			System.out.println("SetValue: " + in);
			Iterator<Map.Entry<String, JsonNode>> fields = in.fields();
			if (fields.hasNext()) {
				Map.Entry<String, JsonNode> entry = fields.next();
				String key = entry.getKey();
				JsonNode value = entry.getValue();
				int id = Integer.parseInt(key);
				var node = graph.findByID(id);
				if(node instanceof StringNode vn){
					vn.set(value.asText());
				}
			}
			in.removeAll();

		}

		return new EndpointJsonResponse(a, "Setting the value");
	}
}
