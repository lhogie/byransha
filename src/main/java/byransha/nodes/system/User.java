package byransha.nodes.system;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.graph.BBGraph;
import byransha.graph.BNode;
import byransha.nodes.primitive.StringNode;
import byransha.web.EndpointResponse;
import byransha.web.EndpointTextResponse;
import byransha.web.NodeEndpoint;
import byransha.web.TechnicalView;
import byransha.web.WebServer;

public class User extends BNode {
	public final StringNode name;
	public int passwordHash;
	public final StringNode passwordNode;
	public final HistoryNode history;
	private BNode currentNode = this;
	public final List<NavigationListener> listeners = new ArrayList<>();

	public User(BBGraph g, String user, int passwordHash) {
		super(g);
		name = new StringNode(g, user, ".+");
		this.passwordHash = passwordHash;
		passwordNode = new StringNode(g, null, ".+");
		history = new HistoryNode(g);
	}

	@Override
	public String whatIsThis() {
		return "a user of the system";
	}

	public BNode currentNode() {
		return currentNode;
	}

	@Override
	public boolean canSee(User user) {
		return user == this;
	}

	@Override
	public boolean canEdit(User user) {
		return user == this;
	}

	public boolean accept(String username, String p) {
		return name.get().equals(username) && passwordNode.get().equals(p);
	}

	public static class UserView extends NodeEndpoint<User> implements TechnicalView {

		public UserView(BBGraph g) {
			super(g);
		}

		@Override
		public String whatItDoes() {
			return "show some things about users";
		}

		@Override
		public boolean sendContentByDefault() {
			return false;
		}

		@Override
		public EndpointResponse exec(ObjectNode input, User user, WebServer webServer, HttpsExchange exchange,
				User node) throws Throwable {
			return new EndpointTextResponse("text/html", pw -> {
				pw.println("<ul>");
				pw.print("<li>Navigation history: ");
				// user.stack.forEach(n -> pw.print(linkTo(n, "X")));
				pw.println("<li>admin? " + false);
				pw.println("</ul>");
			});
		}
	}

	@Override
	public String prettyName() {
		return name.get();
	}

	public static interface NavigationListener {
		void userJumpedTo(BNode n);
	}

	public void jumpTo(BNode n) {
		if (currentNode != n) {
			if (currentNode.historize) {
				history.addToHistory(currentNode);
			}

			currentNode = n;
			listeners.forEach(l -> l.userJumpedTo(n));
		}
	}

	public void back() {
		currentNode = history.back();
		listeners.forEach(l -> l.userJumpedTo(currentNode));
	}

	public void forward() {
		currentNode = history.forward();
		listeners.forEach(l -> l.userJumpedTo(currentNode));
	}
}
