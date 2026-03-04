package byransha.nodes.system;

import java.awt.Color;

import byransha.graph.BGraph;
import byransha.nodes.primitive.ColorNode;

public class UIPreferences extends SystemB {
	ColorNode backgroundColor;
	ColorNode textColor;

	public UIPreferences(BGraph g) {
		super(g);
		backgroundColor = new ColorNode(g);
		backgroundColor.set(Color.white);
		textColor = new ColorNode(g);
		textColor.set(Color.black);
	}

	@Override
	public String prettyName() {
		return "UI preferences";
	}

	@Override
	public String whatIsThis() {
		return "UI preferences";
	}
}
