package byransha.web.endpoint;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.BBGraph;
import byransha.BNode;
import byransha.DateNode;
import byransha.EmailNode;
import byransha.StringNode;
import byransha.User;
import byransha.web.EndpointJsonResponse;
import byransha.web.NodeEndpoint;
import byransha.web.WebServer;

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
		}

		return new EndpointJsonResponse(a, "Setting the value");
	}
}
