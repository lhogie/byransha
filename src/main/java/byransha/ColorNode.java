package byransha;

import java.awt.Color;

public class ColorNode extends ValuedNode<Color> {

	public ColorNode(BBGraph g) {
		super(g);
		this.setMimeType("text/hex");
	}

	public ColorNode(BBGraph g, int id) {
		super(g, id);
	}

	@Override
	public void fromString(String s) {
		set(Color.getColor(s));
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
