package byransha.nodes.primitive;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import byransha.graph.BGraph;
import byransha.graph.NodeError;
import byransha.graph.view.StringNodeView;

public class StringNode extends PrimitiveValueNode<String> {
	String re;
	public boolean hideText;

	public StringNode(BGraph db) {
		super(db);
	}

	@Override
	public void createViews() {
		cachedViews.add(new StringNodeView(g, this));
		super.createViews();
	}


	public StringNode(BGraph g, String init, String re) {
		this(g);
		this.re = re;
		set(init);
	}

	@Override
	protected byte[] valueToBytes(String s) throws IOException {
		return s.getBytes(StandardCharsets.UTF_8);
	}

	@Override
	protected String bytesToValue(byte[] bytes) throws IOException {
		return new String(bytes, StandardCharsets.UTF_8);
	}

	@Override
	public String prettyName() {
		return get();
	}

	@Override
	public void fromString(String s) {
		set(s);
	}

	@Override
	public String whatIsThis() {
		return "a sequence of characters";
	}

	@Override
	protected void fillErrors(List<NodeError> errs) {
		if (re != null) {
			var s = get();

			if (s == null) {
				errs.add(new NodeError(this, "no value"));
			} else if (!s.matches(re)) {
				errs.add(new NodeError(this, "does not match " + re));
			}
		}
	}

	@Override
	public String defaultValue() {
		return null;
	}

}
