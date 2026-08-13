package byransha.lab;

import byransha.Element;
import byransha.ID;
import byransha.InstantiationParameters;

public class MCF extends Status {
	public MCF(InstantiationParameters p) {
		super(p);
		name.set("Maitre de Conference");
	}
	
	public MCF(Element parent, ID id) {
		super(parent, id);
	}
}
