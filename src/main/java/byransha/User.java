package byransha;

import java.util.Stack;

import javax.net.ssl.SSLSession;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.web.EndpointResponse;
import byransha.web.EndpointTextResponse;
import byransha.web.NodeEndpoint;
import byransha.web.TechnicalView;
import byransha.web.WebServer;
import toools.text.TextUtilities;

public class User extends BNode {

	@Override
	public String getDescription() {
		return "User: " + name.get();
	}

	public StringNode name;
	public StringNode passwordNode;
	public Stack<BNode> stack = new Stack<BNode>();
	public SSLSession session;

	public User(BBGraph g, String u, String password) {
		super(g);
		name = new StringNode(g, null);
		name.setAsLabelFor(this);
		name.set(u);

		passwordNode = new StringNode(g, null);
		passwordNode.set(password);
	}

	public User(BBGraph g, int id){
		super(g, id);
		name = new StringNode(g, null);
		name.setAsLabelFor(this);
		passwordNode = new StringNode(g, null);
		passwordNode.set(null);
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
		public String getDescription() {
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
				pw.println("<li>Session ID: "
						+ (user.session.isValid() ? TextUtilities.toHex(user.session.getId()) : "no active session"));
				pw.println("</ul>");
			});
		}

	}
}
