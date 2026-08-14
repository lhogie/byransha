package lab;

import byransha.Element;
import byransha.ID;
import byransha.InstantiationParameter;

public class DR extends Status {
	public DR(InstantiationParameter p) {
		super(p);
		name.set("Directeur de Recherche");
	}

	public DR(Element parent, ID id) {
		super(parent, id);
	}
}
