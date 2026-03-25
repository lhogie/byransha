package byransha.graph.action;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.NodeAction;
import byransha.nodes.primitive.BooleanNode;
import byransha.nodes.system.ChatNode;

public abstract class ConfirmRequiredNodeAction<A extends BNode, R extends BNode> extends NodeAction<A, R> {
	private final BooleanNode confirmed;

	public ConfirmRequiredNodeAction(BGraph g, A action, String category) {
		super(g, action, category);
		this.confirmed = new BooleanNode(g, null);
	}

	@Override
	public final ActionResult exec(ChatNode chat) {
		boolean confirmedByTheUser = confirmed.get();
		return confirmedByTheUser ? execConfirmed() : null;
	}

	protected abstract ActionResult<A, R> execConfirmed();
}