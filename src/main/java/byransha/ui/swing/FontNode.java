package byransha.ui.swing;

import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JLabel;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.nodes.primitive.TradUINodeView;

public class FontNode extends BNode {

	public final Font font;

	protected FontNode(BGraph g, Font font) {
		super(g);
		this.font = font;
	}

	@Override
	public void createViews() {
		cachedViews.elements.add(new FontView(g, this));
		super.createViews();
	}

	@Override
	public String whatIsThis() {
		return "a font";
	}

	@Override
	public String toString() {
		return font.getName();
	}

	public static class FontView extends TradUINodeView<FontNode> {
		public FontView(BGraph g, FontNode node) {
			super(g, node);
		}

		@Override
		public String whatItShows() {
			return "a font";
		}

		@Override
		protected boolean allowsEditing() {
			return false;
		}

		@Override
		public JComponent getComponent() {
			var l = new JLabel(viewedNode.font.getFontName());
			l.setFont(viewedNode.font);
			return l;
		}

	}

}
