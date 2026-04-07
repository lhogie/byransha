package byransha.graph.view;

import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import byransha.graph.BGraph;
import byransha.nodes.primitive.StringNode;
import byransha.ui.swing.ChatSheet;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

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
	public JsonNode jsonView() {
		ObjectNode r = new ObjectNode(factory);
		r.put("value", viewedNode.get());
		r.put("password", viewedNode.hideText);
		return r;
	}

	@Override
	public void writeTo(ChatSheet pane) {
		String s = viewedNode.get();
		var tf = viewedNode.hideText ? new JPasswordField(s) : new JTextField(s);

		tf.setEditable(!viewedNode.readOnly);
		tf.setColumns(20);
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
				viewedNode.set(tf.getText());
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
			}
		});

		viewedNode.valueChangeListeners.add((n, old, newValue) -> {
			SwingUtilities.invokeLater(() -> {
				int caret = tf.getCaretPosition();
				
				if (!tf.getText().equals(newValue)) {
					tf.setText(newValue);
				}

				//tf.setCaretPosition(caret);
			});
		});

		pane.currentLine.add(tf);
	}



}