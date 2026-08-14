package lab;

import byransha.Element;
import byransha.ID;
import byransha.InstantiationParameters;

public class Prof extends Status {
	public Prof(InstantiationParameters p) {
		super(p);
		name.set("Professeur des Universités");
	}

	public Prof(Element parent, ID id) {
		super(parent, id);
	}

}
