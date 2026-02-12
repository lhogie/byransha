package byransha.nodes.primitive;

import java.io.IOException;

import byransha.graph.BBGraph;
import byransha.nodes.system.User;

public class BooleanNode extends PrimitiveValueNode<ByBoolean> {

	public BooleanNode(BBGraph g, User creator, ByBoolean v) {
		super(g, creator);
		set(v, creator);
	}


	@Override
	public String prettyName() {
		return get().toString();
	}

	@Override
	protected ByBoolean bytesToValue(byte[] bytes, User user) throws IOException {
		if (bytes.length != 1)
			throw new IOException("Invalid byte array length for BooleanNode: " + bytes.length);

		if (bytes[0] == 0) {
			return ByBoolean.NO;
		} else if (bytes[0] == 1) {
			return ByBoolean.YES;
		} else {
			return ByBoolean.DUNNO;
		}
	}

	@Override
	protected byte[] valueToBytes(ByBoolean b) throws IOException {
		var r = new byte[1];

		if (b == ByBoolean.NO) {
			r[0] = 0;
		} else if (b == ByBoolean.YES) {
			r[0] = 1;
		} else {
			r[0] = 2;
		}
		
		return r;
	}

	@Override
	public void fromString(String s, User user) {
		set(ByBoolean.valueOf(s), user);
	}

	@Override
	public String whatIsThis() {
		return "a boolean value, (true of false)";
	}

	@Override
	public ByBoolean defaultValue() {
		return ByBoolean.DUNNO;
	}
}
