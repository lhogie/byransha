package byransha.lab.device;

import byransha.Element;
import byransha.ID;
import byransha.lab.LabElement;
import byransha.primitive.LongNode;
import byransha.primitive.URLNode;

public class CPU extends LabElement {
	LongNode nbCores;
	URLNode url;

	public CPU(Element parent, ID id) {
		super(parent, id);
	}

}
