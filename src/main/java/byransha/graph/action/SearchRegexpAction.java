package byransha.graph.action;

import byransha.graph.BBGraph;
import byransha.graph.BNode;
import byransha.nodes.primitive.StringNode;

public class SearchRegexpAction extends SearchAction {
	public StringNode regexp;

	public SearchRegexpAction(BBGraph g) {
		super(g);
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