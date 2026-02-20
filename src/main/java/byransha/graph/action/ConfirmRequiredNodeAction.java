package byransha.graph.action;

import byransha.graph.BBGraph;
import byransha.graph.BNode;
import byransha.graph.NodeAction;
import byransha.nodes.primitive.BooleanNode;
import byransha.nodes.system.User;

public abstract class ConfirmRequiredNodeAction<A extends NodeAction, R extends BNode> extends NodeAction<A, R> {
	private final BooleanNode confirm;

	public ConfirmRequiredNodeAction(BBGraph g) {
		super(g);
		this.confirm = new BooleanNode(g, null);
	}

	@Override
	public ActionResult exec(A a) {
		return confirm.get() ? execConfirm() : null;
	}

	protected abstract ActionResult<A, R> execConfirm();
}