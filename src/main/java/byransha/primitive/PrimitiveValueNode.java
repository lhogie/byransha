package byransha.primitive;

import byransha.Element;
import byransha.ID;

public abstract class PrimitiveValueNode<V> extends ValuedElement<V> {

	public PrimitiveValueNode(Element parent, ID id) {
		super(parent, id);
	}

}
