package byransha.nodes.primitive;

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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.NodeError;
import byransha.graph.view.NodeView;
import byransha.ui.swing.Sheet;

public class LongNode extends PrimitiveValueNode<Long> {
	public static record Bounds(long min, long max) {

	}

	public Bounds bounds;

	public LongNode(BGraph g) {
		super(g);
	}

	public LongNode(BNode parent, long value) {
		super(parent.g);
		set(value);
	}

	@Override
	public void createViews() {
		cachedViews.elements.add(new LongNodeView(this));
		super.createViews();
	}

	public void setBounds(Bounds b) {
		this.bounds = b;
	}

	@Override
	public Long valueFromString(String s) {
		return Long.valueOf(s);
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
		var v = get();

		if (v != null && bounds != null) {
			if (v < bounds.min) {
				errs.add(new NodeError(this, "too small, min is " + bounds.min));
			} else if (v > bounds.max) {
				errs.add(new NodeError(this, "too large, max is " + bounds.max));
			}
		}
	}

	public static class LongNodeView extends NodeView<LongNode> {

		public LongNodeView(LongNode i) {
			super(i.g, i);
		}

		@Override
		public JsonNode jsonView() {
			var l = viewedNode.get();
			return l != null ? new com.fasterxml.jackson.databind.node.LongNode(l) : new TextNode("");
		}

		@Override
		public String whatItShows() {
			return "number editor";
		}

		@Override
		protected boolean allowsEditing() {
			return true;
		}

		@Override
		public void writeTo(Sheet sheet) {
			var tf = new JTextField(String.valueOf(viewedNode.get()));
			tf.setColumns(10);
			tf.setEditable(!viewedNode.readOnly);
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
					viewedNode.set(Long.valueOf(tf.getText()));
				}

				@Override
				public void changedUpdate(DocumentEvent e) {
				}
			});

			viewedNode.valueChangeListeners.add((n, old, newValue) -> {
				SwingUtilities.invokeLater(() -> {
					int caret = tf.getCaretPosition();

					if (!tf.getText().equals(newValue)) {
						tf.setText("" + newValue);
					}

					// tf.setCaretPosition(caret);
				});
			});

			if (viewedNode.bounds != null) {
				var slider = new JSlider((int) viewedNode.bounds.min, (int) viewedNode.bounds.max);
				slider.setValue(viewedNode.get().intValue());
				slider.setEnabled(!viewedNode.readOnly);
				viewedNode.valueChangeListeners.add((n, o, newValue) -> slider.setValue(newValue.intValue()));
				sheet.appendToCurrentLine(slider);
			}
		}

	}
}
