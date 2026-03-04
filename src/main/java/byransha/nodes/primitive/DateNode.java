package byransha.nodes.primitive;

import java.io.IOException;
import java.time.OffsetDateTime;

import byransha.graph.BGraph;

public class DateNode extends PrimitiveValueNode<OffsetDateTime> {
	public DateNode(BGraph g) {
		super(g);
	}

	@Override
	protected OffsetDateTime bytesToValue(byte[] bytes) throws IOException {
		if (bytes == null || bytes.length == 0) {
			return null;
		}
		try {
			return OffsetDateTime.parse(new String(bytes));
		} catch (Exception e) {
			throw new IOException("Failed to parse date from bytes", e);
		}
	}

	@Override
	protected byte[] valueToBytes(OffsetDateTime offsetDateTime) throws IOException {
		if (offsetDateTime == null) {
			return new byte[0];
		}
		try {
			return offsetDateTime.toString().getBytes();
		} catch (Exception e) {
			throw new IOException("Failed to convert date to bytes", e);
		}
	}

	@Override
	public OffsetDateTime defaultValue() {
		return OffsetDateTime.now();
	}

	@Override
	public String whatIsThis() {
		return "DateNode";
	}

	@Override
	public String prettyName() {
		return "Date";
	}

	@Override
	public void fromString(String s) {
		try {
			this.set(OffsetDateTime.parse(s));
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid date format: " + s, e);
		}
	}

}
