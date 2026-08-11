package byransha.ui.swing;

import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JLabel;

import byransha.Chat;
import byransha.Element;
import byransha.service.system.Hub;

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
	public JComponent getListItemComponent(Chat chat) {
		var l = new JLabel();
		l.setFont(font);
		l.setText(font.getFontName());
		return l;
	}

}
