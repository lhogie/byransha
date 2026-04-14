package byransha.ui;

import java.awt.Color;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.ui.swing.ColorPalette;
import byransha.ui.swing.ColorPalette.Style;

public class ColorSchemeNode extends BNode {

	public final Style style;

	public ColorSchemeNode(BGraph g, Style style2) {
		super(g);
		this.style = style2;
	}

	@Override
	public String whatIsThis() {
		return "a color scheme";
	}

	@Override
	public String toString() {
		return style.name();
	}
	
	public Color[] get() {
		return ColorPalette.forStyle(style);
	}

}