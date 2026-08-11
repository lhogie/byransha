package byransha.ui.swing;

import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JLabel;

import byransha.graph.Element;
import byransha.graph.Hub;
import byransha.system.ChatNode;

public class FontNode extends Element {

	public final Font font;

	protected FontNode(Hub g, Font font) {
		super(g, null);
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
	public JComponent getListItemComponent(ChatNode chat) {
		var l = new JLabel();
		l.setFont(font);
		l.setText(font.getFontName());
		return l;
	}

}
