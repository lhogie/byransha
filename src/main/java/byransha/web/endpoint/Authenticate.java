package byransha.web.endpoint;

import java.time.Duration;

import byransha.BBGraph;
import byransha.web.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.BNode;
import byransha.User;
import byransha.web.util.TokenUtil;

public class Authenticate extends NodeEndpoint<BNode> {
	private SessionStore sessionStore;

	@Override
	public String whatItDoes() {
		return "Authenticate endpoint for user login.";
	}

	public Authenticate(BBGraph db) {
		super(db);
	}

	public Authenticate(BBGraph db, SessionStore sessionStore) {
		super(db);
		if (sessionStore == null) {
			throw new IllegalArgumentException("SessionStore cannot be null");
		}
		this.sessionStore = sessionStore;
	}

	public Authenticate(BBGraph db, int id) {
		super(db, id);
		WebServer webServerInstance = findWebServerInstance(db); // Helper method needed

		if (webServerInstance != null && webServerInstance.getSessionStore() != null) {
			this.sessionStore = webServerInstance.getSessionStore();
		} else {
			System.err.println(
					"[ERROR] SessionStore not available during persisted endpoint loading for Authenticate ID: " + id);
			throw new IllegalStateException("SessionStore not available during persisted endpoint loading.");
		}
	}

	@Override
	public boolean canExec(User user) {
		return true;
	}

	@Override
	public boolean requiresAuthentication() {
		return false;
	}

	public void setSessionStore(SessionStore sessionStore) {
		if (sessionStore == null) {
			throw new IllegalArgumentException("SessionStore cannot be null");
		}
		this.sessionStore = sessionStore;
	}

	private WebServer findWebServerInstance(BBGraph graph) {
		if (graph == null)
			return null;
		return graph.find(WebServer.class, ws -> true);
	}

	public static void setSessionCookie(HttpsExchange https, String name, String value) {
		long maxAgeSeconds = Duration.ofMillis(SessionStore.ABSOLUTE_TIMEOUT_MILLIS).getSeconds();
		String cookieValue = String.format("%s=%s; Path=/; Max-Age=%d; SameSite=None; Secure; HttpOnly", name, value,
				maxAgeSeconds);
		https.getResponseHeaders().add("Set-Cookie", cookieValue);
	}

	public static void deleteSessionCookie(HttpsExchange https, String name) {
		String cookieValue = String.format(
				"%s=; Path=/; Max-Age=0; Expires=Thu, 01 Jan 1970 00:00:00 GMT; SameSite=None; Secure; HttpOnly", name);
		https.getResponseHeaders().add("Set-Cookie", cookieValue);
	}


	@Override
	public EndpointJsonResponse exec(ObjectNode in, User _ignoredUserParameter, WebServer webServer,
			HttpsExchange https, BNode bnode) throws Throwable {
		String username = requireParm(in, "username").asText();
		String password = requireParm(in, "password").asText();
		User user = auth(username, password);

		if (user == null) {
			deleteSessionCookie(https, "session_token");
			return ErrorResponse.unauthorized("Authentication Failed");
		} else {
			// OWASP: Generate new Session ID & CSRF token on login
			String csrfToken = TokenUtil.generateSecureToken();
			String sessionToken = sessionStore.createSession(user, csrfToken);

			setSessionCookie(https, "session_token", sessionToken);

			ObjectNode responseJson = new ObjectNode(com.fasterxml.jackson.databind.node.JsonNodeFactory.instance);
			responseJson.put("userId", "" + user.id());
			responseJson.put("csrfToken", csrfToken);

			return new EndpointJsonResponse(responseJson, this);
		}
	}

	private User auth(String username, String password) {
		return graph.find(User.class, u -> u.name != null && u.passwordNode != null && u.accept(username, password));
	}
}
