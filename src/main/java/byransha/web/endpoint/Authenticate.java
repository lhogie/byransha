package byransha.web.endpoint;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.BBGraph;
import byransha.User;
import byransha.web.NodeEndpoint;
import byransha.web.EndpointJsonResponse;
import byransha.web.WebServer;

public class Authenticate extends NodeEndpoint<BBGraph> {

	@Override
	public String getDescription() {
		return "Authenticate endpoint for user login.";
	}

	public Authenticate(BBGraph db) {
		super(db);
	}

	public Authenticate(BBGraph db, int id) {
		super(db, id);
	}

	@Override
	public EndpointJsonResponse exec(ObjectNode in, User user, WebServer webServer, HttpsExchange https, BBGraph g)
			throws Throwable {
		user = auth(requireParm(in, "username").asText(), requireParm(in, "password").asText());

		if (user == null) {
			return null;
		} else {
			user.session = https.getSSLSession();
			return new EndpointJsonResponse(new TextNode("" + user.id()), this);
		}

	}

	private User auth(String username, String password) {
		synchronized (graph.nodes) {
			for (var n : graph.nodes) {
				if (n instanceof User u && u.accept(username, password)) {
					return u;
				}
			}
			return null;
		}
	}

}
