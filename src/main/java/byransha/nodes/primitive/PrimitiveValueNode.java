package byransha.nodes.primitive;

import com.fasterxml.jackson.databind.node.ObjectNode;

import byransha.graph.BGraph;

public abstract class PrimitiveValueNode<V> extends ValuedNode<V> {

	public PrimitiveValueNode(BGraph g) {
		super(g);
	}

	@Override
	public ObjectNode toJSONNode() {
		var r = super.toJSONNode();
		r.put("value", getAsString());
		return r;
	}

	public abstract void fromString(String s);
}
