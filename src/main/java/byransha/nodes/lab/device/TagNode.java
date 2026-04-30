package byransha.nodes.lab.device;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import byransha.graph.BNode;
import byransha.nodes.primitive.ValuedNode;

public class TagNode extends ValuedNode<Tag> {

	public TagNode(BNode parent) {
		super(parent);
	}

	@Override
	public Tag defaultValue() {
		return null;
	}

	@Override
	protected void writeValue(Tag v, ObjectOutput out) throws IOException {
		out.writeUTF(v.name);
		out.writeUTF(v.value);
	}

	@Override
	protected Tag readValue(ObjectInput in) throws IOException {
		var t = new Tag();
		t.name = in.readUTF();
		t.value = in.readUTF();
		return t;
	}

}
