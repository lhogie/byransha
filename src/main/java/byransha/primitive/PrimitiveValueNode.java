package byransha.primitive;

import byransha.Element;
import byransha.ID;
import byransha.InstantiationParameter;

public abstract class PrimitiveValueNode<V> extends ValuedElement<V> {

	public PrimitiveValueNode(InstantiationParameter p) {
		super(p);
	}

	public PrimitiveValueNode(Element parent, ID id) {
		super(parent, id);
	}

}
