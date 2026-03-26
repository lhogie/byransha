package byransha.nodes.primitive;

import byransha.graph.BGraph;

public class BooleanNode extends PrimitiveValueNode<Boolean> {

	public BooleanNode(BGraph g, Boolean v) {
		super(g);
		set(v);
	}

	@Override
	public void createViews() {
		cachedViews.elements.add(new BooleanCheckBoxEditor(g, this));
		cachedViews.elements.add(new BooleanRadioButtonEditor(g, this));
		super.createViews();
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
