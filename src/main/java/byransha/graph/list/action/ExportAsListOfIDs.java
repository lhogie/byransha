package byransha.graph.list.action;

import java.util.stream.Collectors;

import byransha.graph.Category.export;
import byransha.graph.Category.list;
import byransha.graph.Element;
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
