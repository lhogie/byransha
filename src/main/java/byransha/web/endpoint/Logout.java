package byransha.web.endpoint;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.BBGraph;
import byransha.User;
import byransha.web.*;

public class Logout extends NodeEndpoint<BBGraph> {

    private final SessionStore sessionStore;

    @Override
    public String whatIsThis() {
        return "Logs the current user out by invalidating their session.";
    }

    public Logout(BBGraph db, SessionStore sessionStore) {
        super(db);
        if (sessionStore == null) {
            throw new IllegalArgumentException("SessionStore cannot be null");
        }
        this.sessionStore = sessionStore;
    }

    public Logout(BBGraph db, int id) {
        super(db, id);

        WebServer webServerInstance = findWebServerInstance(db);
        if (webServerInstance != null && webServerInstance.getSessionStore() != null) {
            this.sessionStore = webServerInstance.getSessionStore();
        } else {
            System.err.println("[ERROR] SessionStore not available during persisted endpoint loading for Logout ID: " + id);
            throw new IllegalStateException("SessionStore not available during persisted endpoint loading.");
        }
    }

    @Override
    public boolean requiresAuthentication() {
        return false;
    }

    private WebServer findWebServerInstance(BBGraph graph) {
        if (graph == null) return null;
        return graph.find(WebServer.class, ws -> true);
    }


    @Override
    public EndpointResponse exec(ObjectNode input, User user, WebServer webServer, HttpsExchange https, BBGraph ignoredNode) throws Throwable {
        String sessionToken = null;
        String cookieHeader = https.getRequestHeaders().getFirst("Cookie");

        if (cookieHeader != null) {
            for (String cookie : cookieHeader.split(";")) {
                cookie = cookie.trim();
                if (cookie.startsWith("session_token=")) {
                    sessionToken = cookie.substring("session_token=".length());
                    break;
                }
            }
        }

        if (sessionToken != null) {
            sessionStore.removeSession(sessionToken);
            Authenticate.deleteSessionCookie(https, "session_token");
        }

        return new EndpointJsonResponse(new TextNode("Logout successful"), this);
    }
}