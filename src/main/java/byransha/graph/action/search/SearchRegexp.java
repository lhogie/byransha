package byransha.graph.action.search;

import byransha.graph.BBGraph;
import byransha.graph.BNode;
import byransha.nodes.primitive.StringNode;

public class SearchRegexp extends Search {
	public StringNode regexp;

	public SearchRegexp(BBGraph g, BNode srcNode) {
		super(g, srcNode);
		regexp = new StringNode(g, ".*", ".+");
	}

	@Override
	protected boolean accept(BNode n) {
		return n.toJSONNode().toString().matches(regexp.get());
	}

	@Override
	public String whatItDoes() {
		return "search all nodes matching a given regexp";
	}
}