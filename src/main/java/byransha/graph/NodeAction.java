package byransha.graph;

import java.awt.Color;
import java.util.List;

import com.fasterxml.jackson.databind.node.ObjectNode;

import byransha.nodes.system.User;

public abstract class NodeAction<T extends BNode, R extends BNode> extends BNode {
	public String name;
	protected boolean stopRequest;

	protected NodeAction(BBGraph g, User creator) {
		super(g, creator);
	}

	@Override
	public ObjectNode toJSONNode(User user, int depth) {
		var r = super.toJSONNode(user, 0);
		r.put("canExecute", canExecute(user));
		return r;
	}

	public boolean canExecute(User user) {
		return true;
	}

	@Override
	public List<NodeAction> actions() {
		final var en = this;
		return List.of();
	}

	public abstract String whatItDoes();

	public abstract ActionResult<T, R> exec(T target, User user) throws Throwable;

	@Override
	public Color getColor() {
		return Color.white;
	}

	@Override
	public String prettyName() {
		return name;
	}

	@Override
	public String whatIsThis() {
		return "an action";
	}

	static class exec extends NodeAction<NodeAction, BNode> {

		protected exec(BBGraph g, User creator) {
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
			var r = target.exec(target, user);
			r.startDateMs = System.currentTimeMillis();
			return r;
		}
	}
}
