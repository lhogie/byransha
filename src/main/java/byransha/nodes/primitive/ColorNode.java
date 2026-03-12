package byransha.nodes.primitive;

import java.awt.Color;

import byransha.graph.BGraph;

public class ColorNode extends PrimitiveValueNode<Color> {

	public ColorNode(BGraph g) {
		super(g);
	}

	@Override
	public void createViews() {
		cachedViews.values.add(new ColorView(g, this));
		super.createViews();
	}

	@Override
	public Color defaultValue() {
		return Color.white;
	}

	@Override
	public void fromString(String s) {
		set(Color.decode(s));
	}

	@Override
	public String whatIsThis() {
		return "a color";
	}

	@Override
	public String prettyName() {
		return get() != null ? get().toString() : "null";
	}

}
