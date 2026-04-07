package byransha.nodes.primitive;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;
import java.util.Objects;

import byransha.graph.BGraph;
import byransha.graph.NodeError;
import byransha.graph.view.StringNodeView;

public class StringNode extends PrimitiveValueNode<String> {
	String re;
	public boolean hideText;

	public StringNode(BGraph g) {
		super(g);
		Objects.requireNonNull(g);
	}

	@Override
	public void createViews() {
		cachedViews.elements.add(new StringNodeView(g, this));
		super.createViews();
	}

	public StringNode(BGraph g, String init, String re) {
		this(g);
		if (g == null)
			throw new NullPointerException();
		this.re = re;
		set(init);
	}

	@Override
	public String toString() {
		return get();
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

	@Override
	protected void writeValue(String v, ObjectOutput out) throws IOException {
		out.writeUTF(v);
	}

	@Override
	protected String readValue(ObjectInput in) throws IOException {
		return in.readUTF();
	}
}
