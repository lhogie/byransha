package byransha.primitive;

import byransha.ID;
import byransha.graph.Element;

public abstract class PrimitiveValueNode<V> extends ValuedElement<V> {

	public PrimitiveValueNode(Element parent, ID id) {
		super(parent, id);
	}

}
