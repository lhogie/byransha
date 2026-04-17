package byransha.graph.action.search;

import byransha.graph.BNode;
import byransha.nodes.primitive.BooleanNode;
import byransha.nodes.primitive.StringNode;

public class SearchText extends Search {
	public final StringNode searchText = new StringNode(this, "", ".*");
	public final BooleanNode caseSensitive = new BooleanNode(this, false);

	public SearchText(BNode src) {
		super(src);
	}

	@Override
	protected boolean accept(BNode n) {
		var s = n.describeAsJSON().toString();
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