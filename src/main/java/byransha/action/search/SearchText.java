package byransha.action.search;

import byransha.Element;
import byransha.primitive.BooleanNode;
import byransha.primitive.StringNode;

public class SearchText extends Search {
	public final StringNode searchText = new StringNode(this, null, "", ".*");
	public final BooleanNode caseSensitive = new BooleanNode(this, null, false);

	public SearchText(Element src) {
		super(src);
	}

	@Override
	protected boolean accept(Element n) {
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