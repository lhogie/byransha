package byransha.nodes.primitive;

import java.util.Base64;

import byransha.graph.BBGraph;
import byransha.nodes.system.User;

public class ByteNode extends PrimitiveValueNode<byte[]> {
	public ByteNode(BBGraph g, User user) {
		super(g, user);
	}

	@Override
	protected byte[] valueToBytes(byte[] v) {
		return v;
	}

	@Override
	protected byte[] bytesToValue(byte[] bytes, User user) {
		return bytes;
	}

	@Override
	public void fromString(String s, User user) {
		if (s == null || s.isEmpty()) {
			set(null, user);
			return;
		}
		set(Base64.getDecoder().decode(s), user);
	}

	@Override
	public String getAsString() {
		if (get() == null) {
			return "";
		}
		return Base64.getEncoder().encodeToString(get());
	}

	@Override
	public String whatIsThis() {
		return "raw data";
	}

	@Override
	public String prettyName() {
		if (get() == null)
			return "0 bytes";
		return get().length + " bytes";
	}

	@Override
	public byte[] defaultValue() {
		return new byte[0];
	}
}
