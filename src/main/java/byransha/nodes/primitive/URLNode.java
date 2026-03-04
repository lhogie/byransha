package byransha.nodes.primitive;

import byransha.graph.BGraph;
import byransha.graph.view.URLNodeView;

public class URLNode extends StringNode {

	public URLNode(BGraph db, String init) {
		super(db, init,
				"/^https?:\\/\\/(?:www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b(?:[-a-zA-Z0-9()@:%_\\+.~#?&\\/=]*)$/");
	}

	@Override
	public void createViews() {
		cachedViews.add(new URLNodeView(g, this));
		super.createViews();
	}

	public String prettyName() {
		return get();
	}

	@Override
	public void fromString(String s) {
		set(s);
	}

	@Override
	public String whatIsThis() {
		return "an URL";
	}
}
