package byransha.nodes.primitive;

import byransha.graph.BGraph;

public abstract class PrimitiveValueNode<V> extends ValuedNode<V> {

	public PrimitiveValueNode(BGraph g) {
		super(g);
	}

	public abstract void fromString(String s);
}
