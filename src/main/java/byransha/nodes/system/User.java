package byransha.nodes.system;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.graph.BBGraph;
import byransha.graph.BNode;
import byransha.nodes.primitive.ListNode;
import byransha.nodes.primitive.StringNode;
import byransha.web.EndpointJsonResponse;
import byransha.web.EndpointResponse;
import byransha.web.EndpointTextResponse;
import byransha.web.NodeEndpoint;
import byransha.web.TechnicalView;
import byransha.web.WebServer;

public class User extends BNode {
	public StringNode name;
	public int passwordHash;
	public StringNode passwordNode;
	public final ListNode<BNode> history;
	private BNode currentNode;

	public User(BBGraph g, String user, int passwordHash) {
		super(g);
		name = new StringNode(g, user, ".+");
		this.passwordHash = passwordHash;
		passwordNode = new StringNode(g, null, ".+");
		history = new ListNode<>(g);
	}

	@Override
	public String whatIsThis() {
		return "a user of the system";
	}

	public BNode currentNode() {
		return history.get().isEmpty() ? null : history.get().getLast();
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

	public static class History extends NodeEndpoint<BNode> implements TechnicalView {

		public History(BBGraph g) {
			super(g);
		}

		@Override
		public String whatItDoes() {
			return "gets the navigation history";
		}

		@Override
		public boolean sendContentByDefault() {
			return true;
		}

		@Override
		public EndpointResponse exec(ObjectNode input, User user, WebServer webServer, HttpsExchange exchange,
				BNode node) throws Throwable {
			var a = new ArrayNode(null);
//			user.stack.forEach(e -> a.add(e.toJSONNode(user, 0)));
			return new EndpointJsonResponse(a, this);
		}
	}

	@Override
	public String prettyName() {
		if (name == null || name.get() == null) {
			return null;
		}
		return name.get();
	}

	public boolean isAdmin() {
		return name.get().equals("admin");
	}

	public void setAdmin(boolean admin) {
		if (admin) {
			name.set("admin");
		} else {
			if (name.get().equals("admin")) {
				name.set("user");
			}
		}
	}

	public static interface UserListener {
		void userJumpedTo(BNode n);
	}

	public final List<UserListener> listeners = new ArrayList<>();

	public void jumpTo(BNode n) {
		if (n.historize) {
			history.get().add(n);
		}

		if (currentNode != n) {
			currentNode = n;
			listeners.forEach(l -> l.userJumpedTo(n));
		}

	}
}
