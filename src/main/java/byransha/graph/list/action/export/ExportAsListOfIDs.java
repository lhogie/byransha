package byransha.graph.list.action.export;

import java.util.stream.Collectors;

import byransha.graph.BNode;
import byransha.graph.Category.export;
import byransha.graph.Category.list;
import byransha.graph.list.action.FunctionAction;
import byransha.graph.list.action.ListNode;
import byransha.nodes.primitive.TextNode;

public class ExportAsListOfIDs extends FunctionAction<ListNode<? extends BNode>, TextNode> {

	public ExportAsListOfIDs(ListNode<? extends BNode> inputNode) {
		super(inputNode, list.class, export.class);
	}

	@Override
	public String whatItDoes() {
		return "export as a list of node IDs";
	}

	@Override
	public void impl() {
		result = new TextNode(parent, "list of node IDs",
				inputNode.elements.stream().map(n -> n.idAsText()).collect(Collectors.joining("\n")));
	}

	@Override
	public boolean applies() {
		return !inputNode.elements.isEmpty();
	}

}
