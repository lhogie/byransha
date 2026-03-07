package byransha.nodes.primitive;

import java.util.List;

import javax.swing.JTextField;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import byransha.graph.BGraph;
import byransha.graph.NodeError;
import byransha.graph.view.NodeView;
import byransha.ui.swing.ByranshaUserPane;

public class LongNode extends PrimitiveValueNode<Long> {
	public long min = 0, max = 10000;

	public LongNode(BGraph g) {
		super(g);
	}

	@Override
	public void createViews() {
		cachedViews.add(new LongNodeView(g, this));
		super.createViews();
	}

	public void setBounds(long min, long max) {
		this.min = min;
		this.max = max;
	}

	@Override
	public String prettyName() {
		return "a numeric value";
	}

	@Override
	public void fromString(String s) {
		set(Long.valueOf(s));
	}

	@Override
	public String whatIsThis() {
		return "a number of between " + min + " and " + max;
	}

	@Override
	public Long defaultValue() {
		return null;
	}

	@Override
	protected void fillErrors(List<NodeError> errs) {
		var v = get();

		if (v < min)
			errs.add(new NodeError(this, "too small, min is " + min));
		else if (v > max)
			errs.add(new NodeError(this, "too large, max is " + max));
	}

	public static class LongNodeView extends NodeView<LongNode> {

		public LongNodeView(BGraph g, LongNode i) {
			super(g, i);
		}

		@Override
		public JsonNode toJSON() {
			var l = n.get();
			return l != null ? new com.fasterxml.jackson.databind.node.LongNode(l) : new TextNode("null");
		}

		@Override
		public void writeTo(ByranshaUserPane pane) {
			var tf = new JTextField("" + n.get());
			n.valueChangeListeners.add(newValue -> tf.setText("" + newValue));
			pane.append(tf);
		}

		@Override
		public String whatItShows() {
			return "editor for a number";
		}

		@Override
		protected boolean allowsEditing() {
			return true;
		}

	}
}
