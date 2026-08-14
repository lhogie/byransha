package lab;

import byransha.Element;
import byransha.ID;
import byransha.InstantiationParameter;

public class MCF extends Status {
	public MCF(InstantiationParameter p) {
		super(p);
		name.set("Maitre de Conference");
	}

	public MCF(Element parent, ID id) {
		super(parent, id);
	}
}
