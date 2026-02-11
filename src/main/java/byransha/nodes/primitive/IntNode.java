package byransha.nodes.primitive;

import byransha.BBGraph;
import byransha.nodes.system.User;
import org.apache.commons.lang3.Conversion;

import java.io.IOException;

public class IntNode extends PrimitiveValueNode<Integer> {

	public IntNode(BBGraph g, User creator) {
		super(g, creator);
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
		return "IntNode with value: " + get();
	}

}


