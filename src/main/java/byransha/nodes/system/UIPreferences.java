package byransha.nodes.system;

import byransha.graph.BBGraph;
import byransha.nodes.primitive.ColorNode;

public class UIPreferences extends SystemB {
	ColorNode backgroundColor;
	ColorNode textColor;

	public UIPreferences(BBGraph g) {
		super(g);
		backgroundColor = new ColorNode(g);
		backgroundColor.set("#A9A9A9");
		textColor = new ColorNode(g);
		textColor.set("#000000");
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
