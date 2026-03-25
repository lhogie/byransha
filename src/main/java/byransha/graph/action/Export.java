package byransha.graph.action;

import java.util.ArrayList;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.NodeAction;
import byransha.graph.action.list.ListNode;
import byransha.nodes.primitive.TextNode;
import byransha.nodes.system.ChatNode;

public final class Export extends NodeAction<BNode, ListNode<TextNode>> {
	public Export(BGraph g, BNode node) {
		super(g, node, "node");
	}

	@Override
	public boolean wantToBeProposedFor(BNode n) {
		return n.getClass() != Authenticate.class;
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
	public ActionResult<BNode, ListNode<TextNode>> exec(ChatNode chat) throws Throwable {
		var r = new ListNode<byransha.nodes.primitive.TextNode>(g, "export texts");
		var csvs = new ArrayList<CSVData>();
		inputNode.toCSVStreams(csvs, true);
		csvs.stream().map(csv -> new byransha.nodes.primitive.TextNode(g, csv.name + "(CSV)", csv.data))
				.forEach(n -> r.get().add(n));
		r.get().add(new byransha.nodes.primitive.TextNode(g, id() + " (JSON)", describeAsJSON().toPrettyString()));
		return new exportNodeResult(g, this, r);
	}

	@Override
	public boolean applies(ChatNode chat) {
		return true;
//		return inputNode instanceof BusinessNode;
	}
}