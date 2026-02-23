package byransha.graph.action.search;

import byransha.graph.BBGraph;
import byransha.graph.BNode;
import byransha.nodes.primitive.BooleanNode;
import byransha.nodes.primitive.StringNode;

public class SearchText extends Search {
	public StringNode searchText;
	public BooleanNode caseSensitive;

	public SearchText(BBGraph g) {
		super(g);
		searchText = new StringNode(g, "", ".*");
		caseSensitive = new BooleanNode(g, false);
	}

	@Override
	protected boolean accept(BNode n) {
		var s = n.toJSONNode().toString();
		var st = searchText.get();

		if (!caseSensitive.get()) {
			s = s.toLowerCase();
			st = st.toLowerCase();
		}

		return s.contains(st);
	}

	@Override
	public String whatItDoes() {
		return "search all nodes containing a given string";
	}
}