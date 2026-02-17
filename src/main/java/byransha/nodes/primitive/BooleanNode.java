package byransha.nodes.primitive;

import java.io.IOException;

import byransha.graph.BBGraph;
import byransha.nodes.system.User;

public class BooleanNode extends PrimitiveValueNode<Boolean> {

	public BooleanNode(BBGraph g, User creator, Boolean v) {
		super(g, creator);
		set(v, creator);
	}

	@Override
	public String prettyName() {
		return get().toString();
	}

	@Override
	protected Boolean bytesToValue(byte[] bytes, User user) throws IOException {
		if (bytes.length != 1)
			throw new IOException("Invalid byte array length for BooleanNode: " + bytes.length);

		if (bytes[0] == 0) {
			return false;
		} else if (bytes[0] == 1) {
			return true;
		} else {
			return null;
		}
	}

	@Override
	protected byte[] valueToBytes(Boolean b) throws IOException {
		var r = new byte[1];

		if (b == null) {
			r[0] = 2;
		} else {
			if (b) {
				r[0] = 1;
			} else {
				r[0] = 0;
			}
		}

		return r;
	}

	@Override
	public void fromString(String s, User user) {
		set(Boolean.valueOf(s), user);
	}

	@Override
	public String whatIsThis() {
		return "a boolean value, (true of false)";
	}

	@Override
	public Boolean defaultValue() {
		return null;
	}
}
