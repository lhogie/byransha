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
	public String whatIsThis() {
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

		if (target instanceof StringNode) {
			var value = requireParm(in, "value").asText();
			((StringNode) target).set(value);
		} else if (target instanceof EmailNode) {
			var value = requireParm(in, "value").asText();
			((EmailNode) target).set(value);
		} else if (target instanceof DateNode) {
			var value = requireParm(in, "value").asText();
			((DateNode) target).set(value);
		}
/*
		target.save(file -> {
			System.out.println("Saving the new value in the node : " + target);
		});
*/
		return new NodeInfo(graph).exec(in, user, webServer, exchange, target);
	}
}
