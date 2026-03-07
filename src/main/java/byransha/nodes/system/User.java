package byransha.nodes.system;

import java.util.ArrayList;
import java.util.List;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.NodeAction;
import byransha.graph.action.ActionResult;
import byransha.nodes.primitive.StringNode;

public class User extends BNode {
	public final StringNode name;
	public final StringNode passwordNode;
	public final StringNode argon2Hash;
	public final HistoryNode history;
	public final List<JumpListener> jumpListeners = new ArrayList<>();

	public User(BGraph g, String userName, String passwordHash) {
		super(g);
		name = new StringNode(g, userName, ".+");
		passwordNode = new StringNode(g, null, ".+");
		this.argon2Hash = new StringNode(g, passwordHash, ".*");
		passwordNode.hideText = true;
		history = new HistoryNode(g);

		passwordNode.changeListeners.add(n -> {
			argon2Hash.set(Argon.hash(passwordNode.get()));
		});
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

	@Override
	public String prettyName() {
		return name.get();
	}

	public static interface JumpListener {
		void userJumpedTo(BNode n);
	}

	public void jumpTo(BNode n) {
		if (n instanceof NodeAction a && a.execStraightAway) {
			try {
				ActionResult ar = a.exec();
				jumpTo(ar);

				if (ar.jumpStraightAwayToResult) {
					jumpTo(ar.result);
				}
			} catch (Throwable err) {
				jumpTo(error(err, false));
			}
		} else {
			history.addToHistory(n);
			jumpListeners.forEach(l -> l.userJumpedTo(n));
		}
	}
}
