package byransha.security;

import java.util.function.BiPredicate;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.Category;
import byransha.graph.NodeAction;
import byransha.graph.action.ActionResult;
import byransha.nodes.primitive.StringNode;
import byransha.nodes.system.ChatNode;
import byransha.nodes.system.User;
import byransha.util.Stop;

public abstract class Authenticate extends NodeAction<BNode, User> implements BiPredicate<String, String> {
	public final StringNode username, password;

	public static class security extends Category {
	}

	public Authenticate(BGraph g) {
		super(g, g, security.class);
		username = new StringNode(g, "", ".+");
		password = new StringNode(g, "", ".+");
	}

	@Override
	public ActionResult<BNode, User> exec(ChatNode chat) {
		var u = username.get();
		var p = password.get();

		if (u == null || u.isBlank() || p == null || p.isBlank() || !test(u, p)) {
			return createResultNode(null, true);
		} else {
			var user = g.indexes.byClass.forEachNodeAssignableTo(User.class,
					uu -> Stop.stopIf(uu.name.get().equals(u)));

			return createResultNode(user, true);
		}
	}

	@Override
	public boolean applies(ChatNode chat) {
		return true;
	}

	@Override
	public String whatItDoes() {
		return "authenticate a user using" + authenticationMethods();
	}

	public abstract String authenticationMethods();
}