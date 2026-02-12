package byransha.nodes.primitive;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.Conversion;

import byransha.graph.BBGraph;
import byransha.graph.NodeError;
import byransha.nodes.system.User;

public class IntNode extends PrimitiveValueNode<Integer> {
	public int min = 0, max = 10000;

	public IntNode(BBGraph g, User creator) {
		super(g, creator);
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
	protected Integer bytesToValue(byte[] bytes, User user) throws IOException {
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
	public void fromString(String s, User user) {
		set(Integer.valueOf(s), user);
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
		super.fillErrors(errs);
		int v = get();

		if (v < min)
			errs.add(new NodeError(this, "too small, min is " + min));
		else if (v > max)
			errs.add(new NodeError(this, "too large, max is " + max));
	}
}
