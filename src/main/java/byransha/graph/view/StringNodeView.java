package byransha.graph.view;

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import byransha.graph.BBGraph;
import byransha.nodes.primitive.StringNode;

public class StringNodeView extends NodeView<StringNode> {

	public StringNodeView(BBGraph g) {
		super(g);
	}

	@Override
	public String whatItShows() {
		return "string editor";
	}

	protected  boolean kishanable() {
		return true;
	}

	@Override
	protected boolean allowsEditing() {
		return true;
	}

	@Override
	public JsonNode toJSON(StringNode n) {
		ObjectNode r = new ObjectNode(null);
		r.set("value", new TextNode(n.get()));
		r.set("password", BooleanNode.valueOf(n.password));
		return r;
	}

	@Override
	public JComponent createComponentImpl(StringNode n) {
		String s = n.get();
		boolean multiline = s != null && s.indexOf('\n') >= 0;
		var textComponent = multiline ? new JTextArea(s) : new JTextField(s);
		textComponent.setPreferredSize(new Dimension(200, 20));
		textComponent.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				changed(e);
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				changed(e);
			}

			private void changed(DocumentEvent e) {
				n.set(textComponent.getText());
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
			}
		});

		int caret = textComponent.getCaretPosition();
		n.listeners.add(newValue -> {
			if (!textComponent.getText().equals(newValue)) {
				textComponent.setText(newValue);
			}
		});
		textComponent.setCaretPosition(caret);
		return textComponent;
	}

}