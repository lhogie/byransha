package byransha.nodes.primitive;

import java.io.IOException;

import byransha.graph.BGraph;
import byransha.graph.view.BooleanNodeView;

public class BooleanNode extends PrimitiveValueNode<Boolean> {

	public BooleanNode(BGraph g, Boolean v) {
		super(g);
		set(v);
	}

	@Override
	public void createViews() {
		cachedViews.add(new BooleanNodeView(g, this));
	}

	@Override
	public String prettyName() {
		Boolean v = get();
		
		if (v == null) {
			return "dunno";
		}else {
			return v.toString();
		}
	}

	@Override
	protected Boolean bytesToValue(byte[] bytes) throws IOException {
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
	public void fromString(String s) {
		set(Boolean.valueOf(s));
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
