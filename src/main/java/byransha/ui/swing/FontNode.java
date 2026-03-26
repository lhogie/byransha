package byransha.ui.swing;

import java.awt.Font;

import byransha.graph.BGraph;
import byransha.graph.BNode;

public class FontNode extends BNode {

	public final Font font;

	protected FontNode(BGraph g, Font font) {
		super(g);
		this.font = font;
	}


	@Override
	public String whatIsThis() {
		return "a font";
	}

	@Override
	public String toString() {
		return font.getName();
	}

}
