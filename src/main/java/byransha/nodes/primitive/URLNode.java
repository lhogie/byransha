package byransha.nodes.primitive;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JButton;

import byransha.graph.BGraph;
import byransha.ui.swing.ChatSheet;

public class URLNode extends StringNode {

	public URLNode(BGraph db, String init) {
		super(db, init,
				"/^https?:\\/\\/(?:www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b(?:[-a-zA-Z0-9()@:%_\\+.~#?&\\/=]*)$/");
	}

	public String prettyString() {
		return get();
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
				error(e1);
			}
		});
		pane.appendToCurrentLine(b);
	}
}
