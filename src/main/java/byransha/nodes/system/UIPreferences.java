package byransha.nodes.system;

import java.awt.Color;
import java.util.List;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.nodes.primitive.BooleanNode;
import byransha.nodes.primitive.ColorNode;
import byransha.nodes.primitive.LongNode;
import byransha.ui.swing.ColorPalette;
import byransha.ui.swing.ColorPalette.Style;

public class UIPreferences extends SystemNode {
	public final BooleanNode proposeUnapplicableActions;
	public final ColorSchemeNode colorStyle;
	public final LongNode transparencyForNodeBackground = new LongNode(this, 5);
	public ColorNode backgroundColor = new ColorNode(this, Color.white);

	public class ColorSchemeNode extends BNode {

		public final Style style;

		protected ColorSchemeNode(BGraph g, Style style2) {
			super(g);
			this.style = style2;
		}

		@Override
		public String whatIsThis() {
			return "a color scheme";
		}

		@Override
		public String prettyName() {
			return style.name();
		}

	}

	public UIPreferences(BGraph g) {
		super(g);
		this.proposeUnapplicableActions = new BooleanNode(g, true);
		var schemeNodes = List.of(ColorPalette.Style.values()).stream().map(s -> new ColorSchemeNode(g, s)).toList();
		this.colorStyle = schemeNodes.getFirst();
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
