package byransha.nodes.primitive;

import java.util.List;

import javax.swing.JSlider;
import javax.swing.JTextField;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.NodeError;
import byransha.graph.view.NodeView;
import byransha.ui.swing.ChatSheet;

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
		cachedViews.elements.add(new LongNodeView(g, this));
		super.createViews();
	}

	public void setBounds(Bounds b) {
		this.bounds = b;
	}

	@Override
	public String prettyName() {
		return "" + get();
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

		if (bounds != null) {
			if (v < bounds.min)
				errs.add(new NodeError(this, "too small, min is " + bounds.min));
			else if (v > bounds.max)
				errs.add(new NodeError(this, "too large, max is " + bounds.max));
		}
	}

	public static class LongNodeView extends NodeView<LongNode> {

		public LongNodeView(BGraph g, LongNode i) {
			super(g, i);
		}

		@Override
		public JsonNode toJSON() {
			var l = viewedNode.get();
			return l != null ? new com.fasterxml.jackson.databind.node.LongNode(l) : new TextNode("");
		}

		@Override
		public void writeTo(ChatSheet pane) {
			var tf = new JTextField("" + viewedNode.getValueAsString());
			tf.setColumns(10);
			tf.setEditable(!viewedNode.readOnly);
			viewedNode.changeListeners.add(n -> tf.setText("" + ((LongNode) n).getValueAsString()));
			pane.appendToCurrentFlow(tf);

			if (viewedNode.bounds != null) {
				var slider = new JSlider((int) viewedNode.bounds.min, (int) viewedNode.bounds.max);
				slider.setEnabled(!viewedNode.readOnly);
				viewedNode.valueChangeListeners.add((n, o, newValue) -> slider.setValue(newValue.intValue()));
				pane.appendToCurrentFlow(tf);
			}
		}

		@Override
		public String whatItShows() {
			return "number editor";
		}

		@Override
		protected boolean allowsEditing() {
			return true;
		}

	}
}
