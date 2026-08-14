package lab.device;

import byransha.Element;
import byransha.ID;
import byransha.primitive.LongNode;
import byransha.primitive.URLNode;
import lab.LabElement;

public class CPU extends LabElement {
	LongNode nbCores;
	URLNode url;

	public CPU(Element parent, ID id) {
		super(parent, id);
	}

}
