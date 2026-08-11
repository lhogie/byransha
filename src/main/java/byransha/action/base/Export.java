package byransha.action.base;

import java.util.ArrayList;

import byransha.Element;
import byransha.ID;
import byransha.action.Category.node;
import byransha.list.action.FunctionAction;
import byransha.list.action.ListNode;
import byransha.primitive.TextNode;

public final class Export extends FunctionAction<Element, ListNode<TextNode>> {
	public Export(Element node) {
		super(node, node.class);
		hasButtonOnKishanView = true;
	}

	@Override
	public String whatItDoes() {
		return "export this node using text formats";
	}

	public static class CSVData {
		public String name;
		public String data;
	}

	@Override
	public void impl() throws IllegalArgumentException, IllegalAccessException {
		result = new ListNode<TextNode>(this, new ID(), "export texts", TextNode.class);
		var csvs = new ArrayList<CSVData>();
		inputNode.toCSVStreams(csvs, true);
		csvs.stream().map(csv -> new byransha.primitive.TextNode(this, null, csv.name + " as CSV", csv.data))
				.forEach(n -> result.get().add(n));
		result.elements.add(new byransha.primitive.TextNode(this, null, "JSON", describeAsJSON().toPrettyString()));
	}

	@Override
	public boolean applies() {
		return true;
		// return inputNode instanceof BusinessNode;
	}
}