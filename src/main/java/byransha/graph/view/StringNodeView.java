package byransha.graph.view;

import java.util.function.Consumer;

import javax.swing.JComponent;
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

	public StringNodeView(BBGraph g, StringNode node) {
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
	public JsonNode toJSON(StringNode n) {
		ObjectNode r = new ObjectNode(null);
		r.set("value", new TextNode(n.get()));
		r.set("password", BooleanNode.valueOf(n.password));
		return r;
	}

	@Override
	public void createSwingComponents(Consumer<JComponent> onComponentCreated) {
		String s = node.get();
		var textComponent = new JTextField(s);
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
				node.set(textComponent.getText());
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
			}
		});

		int caret = textComponent.getCaretPosition();
		node.valueChangeListeners.add(newValue -> {
			if (!textComponent.getText().equals(newValue)) {
				textComponent.setText(newValue);
			}
		});
		textComponent.setCaretPosition(caret);
		onComponentCreated.accept(textComponent);
	}

}