package byransha.nodes.primitive;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;
import java.util.Objects;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import byransha.graph.BGraph;
import byransha.graph.NodeError;
import byransha.nodes.system.ChatNode;
import byransha.ui.swing.ChatSheet;

public class StringNode extends PrimitiveValueNode<String> {
	String re;
	public boolean hideText;

	public StringNode(BGraph g) {
		this(g, null, null);
		Objects.requireNonNull(g);
	}

	public StringNode(BGraph g, String init, String re) {
		super(g);
		this.re = re;
		set(init);
	}

	public boolean accept(String s) {
		return s != null && (re == null || re.matches(s));
	}

	@Override
	public String toString() {
		return get();
	}

	@Override
	public String whatIsThis() {
		return "a sequence of characters";
	}

	@Override
	protected void fillErrors(List<NodeError> errs) {
		super.fillErrors(errs);
		var s = get();

		if (re != null && s != null && !s.matches(re)) {
			errs.add(new NodeError(this, "does not match " + re));
		}
	}

	@Override
	public String defaultValue() {
		return null;
	}

	@Override
	protected void writeValue(String v, ObjectOutput out) throws IOException {
		out.writeUTF(v);
	}

	@Override
	protected String readValue(ObjectInput in) throws IOException {
		return in.readUTF();
	}

	@Override
	public void writeKishanView(ChatSheet pane) {
		var s = get();
		var tf = hideText ? new JPasswordField() : new JTextField();

		if (s != null) {
			tf.setColumns(Math.min(5, s.length()));
			tf.setText(s);
		}

		tf.setEditable(!readOnly);
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
				var v = tf.getText();
				set(v);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
			}
		});

		valueChangeListeners.add((n, old, newValue) -> {
			SwingUtilities.invokeLater(() -> {
				int caret = tf.getCaretPosition();

				if (!tf.getText().equals(newValue)) {
					tf.setText(newValue);
				}

				// tf.setCaretPosition(caret);
			});
		});
		pane.currentLine.add(tf);
	}

	@Override
	public JComponent getListItemComponent(ChatNode chat) {
		return new JLabel(get());
	}

}
