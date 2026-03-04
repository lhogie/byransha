package byransha.nodes.primitive;

import java.io.IOException;
import java.util.List;

import javax.swing.JTextField;

import org.apache.commons.lang3.Conversion;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import byransha.graph.BGraph;
import byransha.graph.NodeError;
import byransha.graph.view.NodeView;
import byransha.swing.ByranshaUserPane;

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
	protected Long bytesToValue(byte[] bytes) throws IOException {
		if (bytes.length != 4) {
			throw new IOException("IntNode requires exactly 4 bytes, got " + bytes.length);
		}
		return Conversion.byteArrayToLong(bytes, 0, 0, 4, 4);
	}

	@Override
	protected byte[] valueToBytes(Long integer) throws IOException {
		if (integer == null) {
			throw new IOException("Cannot convert null Integer to bytes");
		}
		byte[] result = new byte[4];
		return Conversion.longToByteArray(integer, 0, result, 0, 4);
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
			ObjectNode r = new ObjectNode(null);
			r.set("value", new com.fasterxml.jackson.databind.node.LongNode(n.get()));
			return r;
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
