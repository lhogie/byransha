package byransha.nodes.primitive;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import byransha.graph.BGraph;

public class BooleanNode extends PrimitiveValueNode<Boolean> {

	public BooleanNode(BGraph g, Boolean v) {
		super(g);
		set(v);
	}

	@Override
	public void createViews() {
		cachedViews.elements.add(new BooleanCheckBoxEditor(g, this));
		cachedViews.elements.add(new BooleanRadioButtonEditor(g, this));
		super.createViews();
	}


	@Override
	public String whatIsThis() {
		return "a boolean value, (true of false)";
	}

	@Override
	public Boolean defaultValue() {
		return null;
	}

	@Override
	protected void writeValue(Boolean v, ObjectOutput out) throws IOException {
		out.write(v ? 1 : 0);
	}

	@Override
	protected Boolean readValue(ObjectInput in) throws IOException {
		return in.readInt() == 1;
	}

}
