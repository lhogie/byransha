package byransha.ui.swing;

import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JLabel;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.nodes.system.ChatNode;

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

	@Override
	public JComponent getAsComponent(ChatNode chat) {
		var l = new JLabel();
		l.setFont(font);
		l.setText(font.getFontName());
		return l;
	}

}
