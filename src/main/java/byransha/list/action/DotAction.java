package byransha.list.action;

import byransha.action.Category.export;
import byransha.action.Category.list;
import byransha.primitive.TextNode;

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
		result = new TextNode(this, null, "DOT", inputNode.toDot());
	}

	@Override
	public boolean applies() {
		return inputNode.elements.size() > 0;
	}
}