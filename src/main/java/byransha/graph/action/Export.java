package byransha.graph.action;

import java.util.ArrayList;

import byransha.graph.BNode;
import byransha.graph.list.action.FunctionAction;
import byransha.graph.list.action.ListNode;
import byransha.nodes.primitive.TextNode;

public final class Export extends FunctionAction<BNode, ListNode<TextNode>> {
	public Export(BNode node) {
		super(node, BNode.node.class);
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
	public void impl() throws Throwable {
		result = new ListNode<byransha.nodes.primitive.TextNode>(g, "export texts");
		var csvs = new ArrayList<CSVData>();
		inputNode.toCSVStreams(csvs, true);
		csvs.stream().map(csv -> new byransha.nodes.primitive.TextNode(g, csv.name + "(CSV)", csv.data))
				.forEach(n -> result.get().add(n));
		result.get().add(new byransha.nodes.primitive.TextNode(g, id() + " (JSON)", describeAsJSON().toPrettyString()));
	}

	@Override
	public boolean applies() {
		return true;
//		return inputNode instanceof BusinessNode;
	}
}