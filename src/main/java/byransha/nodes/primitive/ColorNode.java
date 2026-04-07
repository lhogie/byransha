package byransha.nodes.primitive;

import java.awt.Color;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

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
	public String whatIsThis() {
		return "a color";
	}

	@Override
	protected void writeValue(Color v, ObjectOutput out) throws IOException {
		out.write(v.getRed());
		out.write(v.getGreen());
		out.write(v.getBlue());
		out.write(v.getAlpha());
	}

	@Override
	protected Color readValue(ObjectInput in) throws IOException {
		return new Color(in.read(), in.read(), in.read(), in.read());
	}

}
