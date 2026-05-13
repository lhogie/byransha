package byransha.nodes.primitive;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import byransha.graph.BNode;

public abstract class PrimitiveValueNode<V> extends ValuedNode<V> {

	public PrimitiveValueNode(BNode parent) {
		super(parent);
	}

}
