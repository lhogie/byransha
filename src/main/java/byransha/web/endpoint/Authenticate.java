package byransha.web.endpoint;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.BBGraph;
import byransha.User;
import byransha.web.NodeEndpoint;
import byransha.web.EndpointJsonResponse;
import byransha.web.WebServer;

import java.util.UUID;

public class Authenticate extends NodeEndpoint<BBGraph> {

	@Override
	public String whatIsThis() {
		return "Authenticate endpoint for user login.";
	}

	public Authenticate(BBGraph db) {
		super(db);
	}

	public Authenticate(BBGraph db, int id) {
		super(db, id);
	}

	private static void setCookie(HttpsExchange https, String name, String value) {
		String cookie = name + "=" + value + "; Path=/; Max-Age=31536000; SameSite=None; Secure; HttpOnly";
		https.getResponseHeaders().add("Set-Cookie", cookie);
	}

	public static User setDefaultUser(BBGraph g, HttpsExchange https) {
		User user = new User(g, "user", "test");
		System.out.println("creating new user " + user + " with token " + user.token);
		user.stack.push(g.root());
		setCookie(https, "user_token", user.token);
		return user;
	}

	@Override
	public EndpointJsonResponse exec(ObjectNode in, User user, WebServer webServer, HttpsExchange https, BBGraph g)
			throws Throwable {
		user = auth(requireParm(in, "username").asText(), requireParm(in, "password").asText());

		if (user == null) {
			return null;
		} else {
			user.token = UUID.randomUUID().toString();
			setCookie(https, "user_token", user.token);
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
