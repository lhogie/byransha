package byransha;

import java.awt.Color;

public class ColorNode extends ValuedNode<Color> {

	public ColorNode(BBGraph g) {
		super(g);
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
		return "color";
	}

}
