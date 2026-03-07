package byransha.graph.view;

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import byransha.graph.BGraph;
import byransha.nodes.primitive.TextNode;
import byransha.ui.swing.ByranshaUserPane;
import byransha.ui.swing.ResizableByGrip;
import byransha.ui.swing.Utils;

public class TextNodeView extends NodeView<TextNode> {

	public TextNodeView(BGraph g, TextNode node) {
		super(g, node);
	}

	@Override
	public String whatItShows() {
		return "text editor";
	}

	protected boolean kishanable() {
		return false;
	}

	@Override
	protected boolean allowsEditing() {
		return true;
	}

	@Override
	public JsonNode toJSON() {
		return new ObjectNode(factory).put("value", viewedNode.get());
	}

	@Override
	public void writeTo(ByranshaUserPane pane) {
		String s = viewedNode.get();
		var p = new JTextPane();
		p.setText(s);
		p.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				changed(e);
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				changed(e);
			}

			private void changed(DocumentEvent e) {
				viewedNode.set(p.getText());
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
			}
		});

		int caret = p.getCaretPosition();
		viewedNode.changeListeners.add(n -> {
			var newValue = ((TextNode) n).get();

			if (!p.getText().equals(newValue)) {
				p.setText(newValue);
			}
		});
		p.setCaretPosition(caret);
		
		
		pane.appendToCurrentFlow(Utils.resizableScrollPane(p));

		
	}



}