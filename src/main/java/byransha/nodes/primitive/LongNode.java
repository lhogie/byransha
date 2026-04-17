package byransha.nodes.primitive;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

import byransha.graph.BNode;
import byransha.graph.NodeError;
import byransha.ui.swing.ChatSheet;

public class LongNode extends PrimitiveValueNode<Long> {
	public static record Bounds(long min, long max) {

	}

	public Bounds bounds;

	public LongNode(BNode parent) {
		super(parent);
	}

	public LongNode(BNode parent, long value) {
		this(parent);
		set(value);
	}

	public void setBounds(Bounds b) {
		this.bounds = b;
	}

	@Override
	public String whatIsThis() {
		return "a numeric value" + (bounds != null ? " in [" + bounds.min + " " + bounds.max + ']' : "");
	}

	@Override
	public Long defaultValue() {
		return null;
	}

	@Override
	protected void fillErrors(List<NodeError> errs) {
		super.fillErrors(errs);
		var v = get();

		if (v != null && bounds != null) {
			if (v < bounds.min) {
				errs.add(new NodeError(this, "too small, min is " + bounds.min));
			} else if (v > bounds.max) {
				errs.add(new NodeError(this, "too large, max is " + bounds.max));
			}
		}
	}

	@Override
	public void writeKishanView(ChatSheet sheet) {
		var tf = new JTextField(String.valueOf(get()));
		tf.setColumns(10);
		tf.setEditable(!readOnly);
		sheet.appendToCurrentLine(tf);

		((PlainDocument) tf.getDocument()).setDocumentFilter(new DocumentFilter() {
			@Override
			public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
					throws BadLocationException {
				if (string.matches("\\d*")) { // Only allow digits
					super.insertString(fb, offset, string, attr);
				}
			}

			@Override
			public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
					throws BadLocationException {
				if (text.matches("\\d*")) { // Only allow digits
					super.replace(fb, offset, length, text, attrs);
				}
			}
		});

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
				var s = tf.getText().trim();
				set(s.isEmpty() ? null : Long.valueOf(s));
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
			}
		});

		valueChangeListeners.add((n, old, newValue) -> {
			SwingUtilities.invokeLater(() -> {
				int caret = tf.getCaretPosition();

				if (!tf.getText().equals(newValue)) {
					tf.setText("" + newValue);
				}

				// tf.setCaretPosition(caret);
			});
		});

		if (bounds != null) {
			var slider = new JSlider((int) bounds.min, (int) bounds.max);
			slider.setPaintTicks(true);
			slider.setPaintLabels(true);

			if (get() != null) {
				slider.setValue(get().intValue());
			}

			slider.addChangeListener(e -> set((long) slider.getValue()));
			slider.setEnabled(!readOnly);
			valueChangeListeners.add((n, o, newValue) -> slider.setValue(newValue.intValue()));
			sheet.appendToCurrentLine(slider);
		}
	}

	@Override
	protected void writeValue(Long v, ObjectOutput out) throws IOException {
		out.writeLong(v);
	}

	@Override
	protected Long readValue(ObjectInput in) throws IOException {
		return in.readLong();
	}
}
