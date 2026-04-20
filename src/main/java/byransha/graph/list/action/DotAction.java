package byransha.graph.list.action;

import byransha.graph.Category.export;
import byransha.graph.Category.list;
import byransha.nodes.primitive.TextNode;

public class DotAction extends FunctionAction<ListNode, TextNode> {
	public DotAction(ListNode node) {
		super(node, list.class, export.class);
	}

	@Override
	public String whatItDoes() {
		return "generates a DOT showing out links";
	}

	@Override
	public void impl() throws Throwable {
		result = new TextNode(parent, "DOT", inputNode.toDot());
	}

	@Override
	public boolean applies() {
		return inputNode.elements.size() > 0;
	}
}