package byransha.nodes.system;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.nodes.primitive.BooleanNode;
import byransha.nodes.primitive.ListNode;
import byransha.ui.swing.ColorPalette;
import byransha.ui.swing.ColorPalette.Style;

public class UIPreferences extends SystemNode {
	public final BooleanNode proposeUnapplicableActions;
	public final ListNode<ColorSchemeNode> colorStyle;

	class ColorSchemeNode extends BNode {

		public Style style;

		protected ColorSchemeNode(BGraph g) {
			super(g);
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
		this.colorStyle = new ListNode<>(g, "color styles");

		for (var style : ColorPalette.Style.values()) {
			var cn = new ColorSchemeNode(g);
			cn.style = style;
			colorStyle.get().add(cn);
		}
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
