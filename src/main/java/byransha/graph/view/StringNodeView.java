package byransha.graph.view;

import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import byransha.graph.BGraph;
import byransha.nodes.primitive.BooleanNode;
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
	public JsonNode toJSON() {
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

		int caret = tf.getCaretPosition();
		viewedNode.changeListeners.add(n -> {
			var newValue = ((StringNode) n).get();
			if (!tf.getText().equals(newValue)) {
				tf.setText(newValue);
			}
		});
		tf.setCaretPosition(caret);
		pane.appendToCurrentFlow(tf);
	}

	@Override
	public void writeTo(Pane pane) {
		String s = viewedNode.get();
		var tf = viewedNode.hideText ? new PasswordField() : new TextField();
		tf.setText(s);
		tf.setPrefColumnCount(50);
		tf.textProperty().addListener((o, old, newValue) -> viewedNode.set(tf.getText()));

		int caret = tf.getCaretPosition();
		viewedNode.changeListeners.add(n -> {
			var newValue = ((StringNode) n).get();
			
			if (!tf.getText().equals(newValue)) {
				tf.setText(newValue);
			}
		});
		tf.positionCaret(caret);
		pane.getChildren().add(tf);
	}

}