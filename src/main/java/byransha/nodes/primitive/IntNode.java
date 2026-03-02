package byransha.nodes.primitive;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.JComponent;
import javax.swing.JTextField;

import org.apache.commons.lang3.Conversion;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import byransha.graph.BBGraph;
import byransha.graph.NodeError;
import byransha.graph.view.NodeView;

public class IntNode extends PrimitiveValueNode<Integer> {
	public int min = 0, max = 10000;

	public IntNode(BBGraph g) {
		super(g);
	}

	@Override
	public void createViews() {
		cachedViews.add(new IntNodeView(g, this));
	}

	public void setBounds(int min, int max) {
		this.min = min;
		this.max = max;
	}

	@Override
	public String prettyName() {
		return "an integer";
	}

	@Override
	protected Integer bytesToValue(byte[] bytes) throws IOException {
		if (bytes.length != 4) {
			throw new IOException("IntNode requires exactly 4 bytes, got " + bytes.length);
		}
		return Conversion.byteArrayToInt(bytes, 0, 0, 4, 4);
	}

	@Override
	protected byte[] valueToBytes(Integer integer) throws IOException {
		if (integer == null) {
			throw new IOException("Cannot convert null Integer to bytes");
		}
		byte[] result = new byte[4];
		return Conversion.intToByteArray(integer, 0, result, 0, 4);
	}

	@Override
	public void fromString(String s) {
		set(Integer.valueOf(s));
	}

	@Override
	public String whatIsThis() {
		return "a number of between " + min + " and " + max;
	}

	@Override
	public Integer defaultValue() {
		return null;
	}

	@Override
	protected void fillErrors(List<NodeError> errs) {
		int v = get();

		if (v < min)
			errs.add(new NodeError(this, "too small, min is " + min));
		else if (v > max)
			errs.add(new NodeError(this, "too large, max is " + max));
	}

	public static class IntNodeView extends NodeView<IntNode> {

		public IntNodeView(BBGraph g, IntNode i) {
			super(g, i);
		}

		@Override
		public JsonNode toJSON(IntNode n) {
			ObjectNode r = new ObjectNode(null);
			r.set("value", new com.fasterxml.jackson.databind.node.IntNode(n.get()));
			return r;
		}

		@Override
		public void createSwingComponents(Consumer<JComponent> onComponentCreated) {
			var tf = new JTextField("" + node.get());
			node.valueChangeListeners.add(newValue -> tf.setText("" + newValue));
			onComponentCreated.accept(tf);
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
