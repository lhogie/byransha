package byransha.graph.view;

import java.awt.Dimension;

import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import byransha.graph.BGraph;
import byransha.nodes.primitive.StringNode;
import byransha.swing.ByranshaUserPane;

public class StringNodeView extends NodeView<StringNode> {

	public StringNodeView(BGraph g, StringNode node) {
		super(g, node);
	}

	@Override
	public String whatItShows() {
		return "string editor";
	}

	protected boolean kishanable() {
		return true;
	}

	@Override
	protected boolean allowsEditing() {
		return true;
	}

	@Override
	public JsonNode toJSON() {
		ObjectNode r = new ObjectNode(factory);
		r.put("value", n.get());
		r.put("password", n.hideText);
		return r;
	}

	@Override
	public void writeTo(ByranshaUserPane pane) {
		String s = n.get();
		var tf = n.hideText ? new JPasswordField(s) : new JTextField(s);
		tf.setPreferredSize(new Dimension(100, 30));
		tf.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				changed(e);
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				changed(e);
			}

			private void changed(DocumentEvent e) {
				n.set(tf.getText());
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
			}
		});

		int caret = tf.getCaretPosition();
		n.valueChangeListeners.add(newValue -> {
			if (!tf.getText().equals(newValue)) {
				tf.setText(newValue);
			}
		});
		tf.setCaretPosition(caret);
		pane.append(tf);
	}

}