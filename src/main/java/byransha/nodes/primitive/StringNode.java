package byransha.nodes.primitive;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import byransha.graph.BBGraph;
import byransha.graph.NodeError;
import byransha.nodes.system.User;

public class StringNode extends PrimitiveValueNode<String> {
	String re;

	public StringNode(BBGraph db, User creator) {
		super(db, creator);
	}

	public StringNode(BBGraph g, User creator, String init, String re) {
		this(g, creator);
		this.re = re;
		set(init, creator);
	}

	@Override
	protected byte[] valueToBytes(String s) throws IOException {
		return s.getBytes(StandardCharsets.UTF_8);
	}

	@Override
	protected String bytesToValue(byte[] bytes, User user) throws IOException {
		return new String(bytes, StandardCharsets.UTF_8);
	}

	@Override
	public String prettyName() {
		return get();
	}

	@Override
	public void fromString(String s, User creator) {
		set(s, creator);
	}

	@Override
	public String whatIsThis() {
		return "a sequence of characters";
	}

	@Override
	protected void fillErrors(List<NodeError> errs, int depth) {
		if (re != null && !get().matches(re)) {
			errs.add(new NodeError(this, "does not match " + re));
		}
	}

	@Override
	public String defaultValue() {
		return null;
	}
}
