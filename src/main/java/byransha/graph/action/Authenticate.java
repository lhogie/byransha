package byransha.graph.action;

import java.util.ArrayList;
import java.util.List;

import byransha.graph.BBGraph;
import byransha.graph.BNode;
import byransha.graph.NodeAction;
import byransha.nodes.primitive.StringNode;
import byransha.nodes.system.User;
import toools.Stop;

public class Authenticate extends NodeAction<BNode, User> {
	public StringNode username, password;

	public Authenticate(BBGraph g, BNode node) {
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
	public ActionResult<BNode, User> exec(BNode target) {
		var u = username.get();
		var p = password.getOrDefault("").hashCode();
		var newUser = g.forEachNodeOfClass(User.class,
				uu -> Stop.stopIf(uu.name.get().equals(u) && uu.passwordHash == p));

		if (newUser != null) {
			g.systemNode.setCurrentUser(newUser);
		}

		return createResultNode(newUser);
	}

	@Override
	public String whatItDoes() {
		return "authenticate a user";
	}
}