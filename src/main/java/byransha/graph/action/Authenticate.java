package byransha.graph.action;

import java.util.ArrayList;
import java.util.List;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.NodeAction;
import byransha.nodes.primitive.StringNode;
import byransha.nodes.system.ChatNode;
import byransha.nodes.system.User;
import byransha.util.Stop;

public class Authenticate extends NodeAction<BNode, User> {
	public StringNode username, password;

	public Authenticate(BGraph g, BNode node) {
		super(g, node);
		username = new StringNode(g, null, ".+");
		password = new StringNode(g, null, ".+");
	}

	@Override
	public List<NodeAction> actions() {
		var r = new ArrayList<NodeAction>();
		r.add(new exec(g, this));
		r.add(new Reset(g, this));
		return r;
	}

	@Override
	public ActionResult<BNode, User> exec(ChatNode chat) {
		var u = username.get();
		var p = password.getOrDefault("").hashCode();
		var newUser = g.indexes.byClass.forEachNodeOfClass(User.class,
				uu -> Stop.stopIf(uu.name.get().equals(u) && uu.argon2Hash.equals(p)));

		if (newUser != null) {
			g.setCurrentUser(newUser);
		}

		return createResultNode(newUser, true);
	}

	@Override
	public boolean applies(ChatNode chat) {
		return true;
	}

	@Override
	public String whatItDoes() {
		return "authenticate a user";
	}
}