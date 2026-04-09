package byransha.nodes.primitive;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import byransha.graph.BGraph;
import byransha.ui.swing.ChatSheet;
import byransha.ui.swing.TranslatableTextArea;
import byransha.ui.swing.Utils;

public class TextNode extends PrimitiveValueNode<String> {
	StringNode labelNode;
	public boolean info;

	public TextNode(BGraph g, String label, String data) {
		super(g);
		set(data);
		labelNode = new StringNode(g, label, ".+");
	}

	@Override
	public void createActions() {
		cachedActions.elements.add(new saveNodeAction(g, this));
		super.createActions();
	}

	@Override
	public String whatIsThis() {
		return "a multiline text";
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
	public void writeTo(ChatSheet pane) {
		String s = get();

		if (info) {
			var ta = new TranslatableTextArea(g.translator);
			ta.setText(s);
			pane.appendToCurrentLine(Utils.resizableScrollPane(ta));
		} else {
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
					set(p.getText());
				}

				@Override
				public void changedUpdate(DocumentEvent e) {
				}
			});

			int caret = p.getCaretPosition();
			valueChangeListeners.add((a, o, n) -> {
				var newValue = get();

				if (!p.getText().equals(newValue)) {
					p.setText(newValue);
				}
			});
			p.setCaretPosition(caret);

			pane.appendToCurrentLine(Utils.resizableScrollPane(p));
		}

	}
}
