package byransha.primitive;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JButton;

import byransha.Element;
import byransha.ID;
import byransha.ui.swing.ChatSheet;

public class URLNode extends StringNode {

	public URLNode(Element parent, ID id, String init) {
		super(parent, id, init,
				"/^https?:\\/\\/(?:www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b(?:[-a-zA-Z0-9()@:%_\\+.~#?&\\/=]*)$/");
	}

	@Override
	public String whatIsThis() {
		return "an URL";
	}

	@Override
	public void writeKishanView(ChatSheet pane) {
		super.writeKishanView(pane);
		var b = new JButton("browse");
		b.addActionListener(e -> {
			try {
				Desktop.getDesktop().browse(new URI(get()));
			} catch (IOException | URISyntaxException e1) {
				hub().errorLog.add(e1);
			}
		});
		pane.appendToCurrentLine(b);
	}
}
