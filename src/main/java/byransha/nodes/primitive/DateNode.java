package byransha.nodes.primitive;

import java.time.OffsetDateTime;

import byransha.graph.BGraph;

public class DateNode extends PrimitiveValueNode<OffsetDateTime> {
	public DateNode(BGraph g) {
		super(g);
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
	public OffsetDateTime valueFromString(String s) {
		try {
			return OffsetDateTime.parse(s);
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid date format: " + s, e);
		}
	}

}
