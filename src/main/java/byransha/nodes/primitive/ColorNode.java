package byransha.nodes.primitive;

import java.awt.Color;
import java.io.IOException;

import byransha.graph.BGraph;

public class ColorNode extends PrimitiveValueNode<Color> {

	public ColorNode(BGraph g) {
		super(g);
	}

	@Override
	public void createViews() {
		cachedViews.add(new ColorView(g, this));
		super.createViews();
	}

	@Override
	public Color defaultValue() {
		return Color.white;
	}

	@Override
	public void fromString(String s) {
		set(Color.decode(s));
	}

	@Override
	protected byte[] valueToBytes(Color c) throws IOException {
		var s = c.toString();
		if (s == null) {
			return new byte[0];
		}
		String cleanHex = s.replace("#", "").trim();
		if (cleanHex.length() != 6) {
			throw new IOException("Color hex string must be 6 characters long");
		}
		byte[] bytes = new byte[3];
		try {
			bytes[0] = (byte) Integer.parseInt(cleanHex.substring(0, 2), 16);
			bytes[1] = (byte) Integer.parseInt(cleanHex.substring(2, 4), 16);
			bytes[2] = (byte) Integer.parseInt(cleanHex.substring(4, 6), 16);
			return bytes;
		} catch (NumberFormatException e) {
			throw new IOException("Invalid hex color format");
		}
	}

	@Override
	protected Color bytesToValue(byte[] bytes) throws IOException {
		if (bytes == null || bytes.length != 3) {
			throw new IOException("Color bytes must be exactly 3 bytes long");
		}
		String hex = String.format("#%02X%02X%02X", bytes[0] & 0xFF, bytes[1] & 0xFF, bytes[2] & 0xFF);
		var c = Color.decode(hex);
		set(c);
		return c;
	}

	@Override
	public String whatIsThis() {
		return "a color";
	}

	@Override
	public String prettyName() {
		return get() != null ? get().toString() : "null";
	}

}
