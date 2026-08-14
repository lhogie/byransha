package lab;

import byransha.Element;
import byransha.ID;
import byransha.InstantiationParameter;

public class Prof extends Status {
	public Prof(InstantiationParameter p) {
		super(p);
		name.set("Professeur des Universités");
	}

	public Prof(Element parent, ID id) {
		super(parent, id);
	}

}
