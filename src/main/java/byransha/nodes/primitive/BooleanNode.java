package byransha.nodes.primitive;

import byransha.graph.BGraph;
import byransha.graph.view.BooleanNodeView;

public class BooleanNode extends PrimitiveValueNode<Boolean> {

	public BooleanNode(BGraph g, Boolean v) {
		super(g);
		set(v);
	}

	@Override
	public void createViews() {
		cachedViews.elements.add(new BooleanNodeView(g, this));
		super.createViews();
	}

	@Override
	public String prettyName() {
		Boolean v = get();

		if (v == null) {
			return "dunno";
		} else {
			return v.toString();
		}
	}

	@Override
	public Boolean valueFromString(String s) {
		if (s.equals("y"))
			return true;

		if (s.equals("n"))
			return true;

		return Boolean.valueOf(s);
	}

	@Override
	public String whatIsThis() {
		return "a boolean value, (true of false)";
	}

	@Override
	public Boolean defaultValue() {
		return null;
	}

}
