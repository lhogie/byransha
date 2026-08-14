package lab;

import byransha.Element;
import byransha.ID;
import byransha.InstantiationParameter;
import byransha.primitive.BooleanNode;

public class Software extends Publication {
	final BooleanNode openSource = fieldNode("surface", id -> new BooleanNode(this, id, null));

	public Software(InstantiationParameter p) {
		super(p);
	}
}
