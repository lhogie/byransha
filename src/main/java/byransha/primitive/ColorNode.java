package byransha.primitive;

import java.awt.Color;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.swing.JColorChooser;
import javax.swing.JComponent;

import byransha.graph.Hub;
import byransha.system.ChatNode;
import byransha.graph.BNode;

public class ColorNode extends PrimitiveValueNode<Color> {

	public ColorNode(Hub g) {
		super(g);
	}

	public ColorNode(BNode parent, Color c) {
		super(parent);
		set(c);
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

	@Override
	public JComponent getListItemComponent(ChatNode chat) {
		var cc = new JColorChooser();
		cc.getSelectionModel().addChangeListener(e -> set(cc.getColor()));
		return cc;
	}

}
