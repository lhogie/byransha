package byransha.nodes.primitive;

import java.util.List;

import byransha.graph.BGraph;
import byransha.graph.NodeError;
import byransha.graph.view.StringNodeView;

public class StringNode extends PrimitiveValueNode<String> {
	String re;
	public boolean hideText;

	public StringNode(BGraph g) {
		super(g);
	}

	@Override
	public void createViews() {
		cachedViews.elements.add(new StringNodeView(g, this));
		super.createViews();
	}

	public StringNode(BGraph g, String init, String re) {
		this(g);
		this.re = re;
		set(init);
	}

	@Override
	public String prettyName() {
		return get();
	}

	@Override
	public String valueFromString(String s) {
		return s;
	}

	@Override
	public String whatIsThis() {
		return "a sequence of characters";
	}

	@Override
	protected void fillErrors(List<NodeError> errs) {
		if (re != null) {
			var s = get();

			if (s == null) {
				errs.add(new NodeError(this, "no value"));
			} else if (!s.matches(re)) {
				errs.add(new NodeError(this, "does not match " + re));
			}
		}
	}

	@Override
	public String defaultValue() {
		return null;
	}

}
