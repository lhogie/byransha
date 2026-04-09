package byransha.graph.action.list;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.Category;
import byransha.graph.NodeAction;
import byransha.graph.action.ActionResult;
import byransha.nodes.system.ChatNode;

public abstract class FilterAction<N extends BNode> extends NodeAction<N, N> {

	public FilterAction(BGraph g, N inputNode, Class<? extends Category>... category) {
		super(g, inputNode, category);
	}

	@Override
	public final ActionResult<N, N> exec(ChatNode chat) throws Throwable {
		apply(inputNode);
		return createResultNode(inputNode, true);
	}

	protected abstract void apply(N node);
}
