package byransha.nodes.primitive;

import java.util.Base64;

import byransha.graph.BGraph;

public class ByteNode extends PrimitiveValueNode<byte[]> {
	public ByteNode(BGraph g) {
		super(g);
	}

	

	@Override
	public void fromString(String s) {
		if (s == null || s.isEmpty()) {
			set(null);
			return;
		}
		set(Base64.getDecoder().decode(s));
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
