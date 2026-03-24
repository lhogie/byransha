package byransha.graph.view;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JButton;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import byransha.graph.BGraph;
import byransha.nodes.primitive.StringNode;
import byransha.ui.swing.Sheet;

public class URLNodeView extends StringNodeView {

	public URLNodeView(BGraph g, StringNode node) {
		super(g, node);
	}

	@Override
	public String whatItShows() {
		return "URL editor";
	}

	@Override
	public JsonNode jsonView() {
		ObjectNode r = new ObjectNode(factory);
		r.put("value", viewedNode.get());
		r.put("password", viewedNode.hideText);
		return r;
	}

	@Override
	public void writeTo(Sheet pane) {
		super.writeTo(pane);
		var b = new JButton("browse");
		b.addActionListener(e -> {
			try {
				Desktop.getDesktop().browse(new URI(viewedNode.get()));
			} catch (IOException | URISyntaxException e1) {
				error(e1);
			}
		});
		pane.appendToCurrentLine(b);
	}

}