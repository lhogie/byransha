package byransha.nodes.primitive;

import java.awt.Color;

import byransha.graph.BGraph;
import byransha.graph.BNode;

public class ColorNode extends PrimitiveValueNode<Color> {

	public ColorNode(BGraph g) {
		super(g);
	}

	public ColorNode(BNode parent, Color c) {
		super(parent.g);
		set(c);
	}

	@Override
	public void createViews() {
		cachedViews.elements.add(new ColorView(g, this));
		super.createViews();
	}

	@Override
	public Color defaultValue() {
		return Color.white;
	}

	@Override
	public Color valueFromString(String s) {
		return Color.decode(s);
	}

	@Override
	public String whatIsThis() {
		return "a color";
	}


}
