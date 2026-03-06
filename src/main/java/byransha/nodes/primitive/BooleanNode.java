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
		cachedViews.add(new BooleanNodeView(g, this));
		super.createViews();
	}

	@Override
	public String prettyName() {
		Boolean v = get();
		
		if (v == null) {
			return "dunno";
		}else {
			return v.toString();
		}
	}


	@Override
	public void fromString(String s) {
		set(Boolean.valueOf(s));
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
