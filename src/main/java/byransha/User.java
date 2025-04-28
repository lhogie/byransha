package byransha;

import java.util.Stack;
import java.util.UUID;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.web.EndpointJsonResponse;
import byransha.web.EndpointResponse;
import byransha.web.EndpointTextResponse;
import byransha.web.NodeEndpoint;
import byransha.web.TechnicalView;
import byransha.web.WebServer;

public class User extends PersistingNode {

	public StringNode name;
	public StringNode passwordNode;
	public Stack<BNode> stack = new Stack<BNode>();

	public User(BBGraph g, String u, String password) {
		super(g);
		name = new StringNode(g, null);
		name.setAsLabelFor(this);
		name.set(u);

		passwordNode = new StringNode(g, null);
		passwordNode.set(password);

		/*
		 * this.saveOuts(f -> {});
		 *
		 * this.saveIns(f -> {}); forEachOut((n, node) -> node.saveIns(f -> {}));
		 * forEachIn((n, node) -> node.saveOuts(f -> {}));
		 */
	}

	public User(BBGraph g, int id) {
		super(g, id);
	}

	@Override
	public String whatIsThis() {
		return "a user of the system";
	}

	public BNode currentNode() {
		return stack.isEmpty() ? null : stack.peek();
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

		public UserView(BBGraph g, int id) {
			super(g, id);
		}

		@Override
		public String whatIsThis() {
			return "UserView for managing user-related operations.";
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
//				user.stack.forEach(n -> pw.print(linkTo(n, "X")));
				pw.println("<li>admin? " + false);
				pw.println("</ul>");
			});
		}
	}

	public static class History extends NodeEndpoint<BNode> implements TechnicalView {
		public History(BBGraph g) {
			super(g);
		}

		public History(BBGraph g, int id) {
			super(g, id);
		}

		@Override
		public String whatIsThis() {
			return "the navigation history";
		}

		@Override
		public boolean sendContentByDefault() {
			return true;
		}

		@Override
		public EndpointResponse exec(ObjectNode input, User user, WebServer webServer, HttpsExchange exchange,
									 BNode node) throws Throwable {
			var a = new ArrayNode(null);
			user.stack.forEach(e -> a.add(e.toJSONNode()));
			return new EndpointJsonResponse(a, this);
		}
	}

	@Override
	public String prettyName() {
		return name.get();
	}
}
