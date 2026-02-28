package byransha.graph.action;

import byransha.graph.BBGraph;
import byransha.graph.BNode;
import byransha.graph.NodeAction;
import byransha.nodes.primitive.BooleanNode;

public abstract class ConfirmRequiredNodeAction<A extends BNode, R extends BNode> extends NodeAction<A, R> {
	private final BooleanNode confirmed;

	public ConfirmRequiredNodeAction(BBGraph g, A action) {
		super(g, action);
		this.confirmed = new BooleanNode(g, null);
	}

	@Override
	protected final ActionResult exec(A a) {
		boolean confirmedByTheUser = confirmed.get();
		return confirmedByTheUser ? execConfirmed(a) : null;
	}

	protected abstract ActionResult<A, R> execConfirmed(A a);
}