package byransha.graph;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.node.ObjectNode;

import byransha.nodes.system.User;
import toools.io.Cout;

public abstract class NodeAction<T extends BNode, R extends BNode> extends BNode {
	protected boolean stopRequest;

	public NodeAction(BBGraph g) {
		super(g, g.systemNode.getCurrentUser());
	}

	@Override
	public ObjectNode toJSONNode(User user, int depth) {
		var r = super.toJSONNode(user, depth);
		r.put("canExecute", canExecute(user));
		return r;
	}

	public boolean canExecute(User user) {
		return true;
	}

	public String name() {
		return getClass().getSimpleName();
	}

	public abstract String whatItDoes();

	public abstract ActionResult<T, R> exec(T target, User user) throws Throwable;

	@Override
	public Color getColor() {
		return Color.white;
	}

	@Override
	public String prettyName() {
		return name();
	}

	@Override
	public String whatIsThis() {
		return "an action which " + whatItDoes();
	}

	public static final Map<Class, List<Class>> actions = new HashMap<>();

	public static void add(Class c, Class v) {
		var l = actions.get(c);

		if (l == null) {
			actions.put(c, l = new ArrayList<>());
		}

		l.add(v);
	}

	static {
		add(BNode.class, BNode.exportNodeAction.class);
		add(BNode.class, BNode.ResetNodeAction.class);
		add(BNode.class, BNode.Delete.class);
		add(BNode.class, SearchAction.class);
		add(BNode.class, SearchTextAction.class);
		add(BNode.class, SearchRegexpAction.class);
		add(NodeAction.class, NodeAction.exec.class);
	}

	static class exec extends NodeAction<NodeAction, BNode> {

		public exec(BBGraph g, User creator) {
			super(g, creator);
		}

		@Override
		public String whatItDoes() {
			return "trigger its action";
		}

		boolean execStraight() {
			return getClass().getEnclosingClass() == NodeAction.class;
		}

		@Override
		public ActionResult exec(NodeAction target, User user) throws Throwable {
			Cout.debugSuperVisible("exec " + target.prettyName());
			var r = target.exec(target, user);
			r.startDateMs = System.currentTimeMillis();
			return r;
		}
	}
}
