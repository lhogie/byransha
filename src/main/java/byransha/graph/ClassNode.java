package byransha.graph;

import java.io.IOException;

import byransha.nodes.primitive.PrimitiveValueNode;

public class ClassNode extends PrimitiveValueNode<Class> {

	public ClassNode(BBGraph g) {
		super(g);
	}

	@Override
	public void fromString(String s) {
		try {
			set(Class.forName(s));
		} catch (ClassNotFoundException e) {
			set(null);
		}
	}

	@Override
	protected byte[] valueToBytes(Class v) throws IOException {
		return v.getName().getBytes();
	}

	@Override
	protected Class bytesToValue(byte[] bytes) throws IOException {
		try {
			return Class.forName(new String(bytes));
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	@Override
	public Class defaultValue() {
		return null;
	}

	@Override
	public String whatIsThis() {
		return "a class";
	}

	@Override
	public String prettyName() {
		return getAsString();
	}

}
