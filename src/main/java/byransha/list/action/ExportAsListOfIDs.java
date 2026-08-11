package byransha.list.action;

import java.util.stream.Collectors;

import byransha.Element;
import byransha.action.Category.export;
import byransha.action.Category.list;
import byransha.primitive.TextNode;

public class ExportAsListOfIDs extends FunctionAction<ListNode<? extends Element>, TextNode> {

	public ExportAsListOfIDs(ListNode<? extends Element> inputNode) {
		super(inputNode, list.class, export.class);
	}

	@Override
	public String whatItDoes() {
		return "export as a list of node IDs";
	}

	@Override
	public void impl() {
		result = new TextNode(this, null, "list of node IDs",
				inputNode.elements.stream().map(n -> n.id().toString()).collect(Collectors.joining("\n")));
	}

	@Override
	public boolean applies() {
		return !inputNode.elements.isEmpty();
	}

}
