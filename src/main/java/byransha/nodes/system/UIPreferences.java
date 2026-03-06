package byransha.nodes.system;

import java.awt.Color;

import byransha.graph.BGraph;
import byransha.nodes.primitive.BooleanNode;
import byransha.nodes.primitive.ColorNode;

public class UIPreferences extends SystemNode {
	public final ColorNode backgroundColor;
	public final ColorNode textColor;
	public final  BooleanNode showUnapplicationActions;

	public UIPreferences(BGraph g) {
		super(g);
		backgroundColor = new ColorNode(g);
		backgroundColor.set(Color.white);
		textColor = new ColorNode(g);
		textColor.set(Color.black);
		this.showUnapplicationActions = new BooleanNode(g, true);
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
