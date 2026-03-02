package byransha.nodes.system;

import java.util.ArrayList;
import java.util.List;

import byransha.graph.BBGraph;
import byransha.graph.BNode;
import byransha.graph.NodeAction;
import byransha.nodes.primitive.StringNode;

public class User extends BNode {
	public final StringNode name;
	public String passwordHash;
	public final StringNode password;
	public final HistoryNode history;
	public final List<NavigationListener> listeners = new ArrayList<>();

	public User(BBGraph g, String userName, String passwordHash) {
		super(g);
		name = new StringNode(g, userName, ".+");
		this.passwordHash = passwordHash;
		password = new StringNode(g, null, ".+");
		history = new HistoryNode(g);
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
		return name.get().equals(username) && password.get().equals(p);
	}

	@Override
	public String prettyName() {
		return name.get();
	}

	public static interface NavigationListener {
		void userJumpedTo(BNode n);
	}

	public void jumpTo(BNode n) {
		if (currentNode() == n)
			throw new IllegalArgumentException("already on this node");

		if (n instanceof NodeAction a && a.execStraightAway) {
			try {
				jumpTo(a.exec());
			} catch (Throwable err) {
				jumpTo(error(err, false));
			}
		} else {
			history.addToHistory(n);
			listeners.forEach(l -> l.userJumpedTo(n));
		}
	}
}
