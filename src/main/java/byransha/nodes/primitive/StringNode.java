package byransha.nodes.primitive;

import java.awt.Dimension;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

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
import byransha.graph.NodeError;
import byransha.graph.NodeView;
import byransha.nodes.system.User;

public class StringNode extends PrimitiveValueNode<String> {
	String re;
	public boolean password;

	public static class StringNodeView extends NodeView<StringNode> {

		public StringNodeView(BBGraph g, User creator) {
			super(g, creator);
		}

		@Override
		public JsonNode toJSON(User requester, StringNode n) {
			ObjectNode r = new ObjectNode(null);
			r.set("value", new TextNode(n.get()));
			r.set("password", BooleanNode.valueOf(n.password));
			return r;
		}

		@Override
		public JComponent createComponentImpl(User requester, StringNode n) {
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
					n.set(textComponent.getText(), requester);
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

	public StringNode(BBGraph db, User creator) {
		super(db, creator);
	}

	public StringNode(BBGraph g, User creator, String init, String re) {
		this(g, creator);
		this.re = re;
		set(init, creator);
	}

	@Override
	protected byte[] valueToBytes(String s) throws IOException {
		return s.getBytes(StandardCharsets.UTF_8);
	}

	@Override
	protected String bytesToValue(byte[] bytes, User user) throws IOException {
		return new String(bytes, StandardCharsets.UTF_8);
	}

	@Override
	public String prettyName() {
		return get();
	}

	@Override
	public void fromString(String s, User creator) {
		set(s, creator);
	}

	@Override
	public String whatIsThis() {
		return "a sequence of characters";
	}

	@Override
	protected void fillErrors(List<NodeError> errs, int depth) {
		if (re != null && !get().matches(re)) {
			errs.add(new NodeError(this, "does not match " + re));
		}
	}

	@Override
	public String defaultValue() {
		return null;
	}
}
