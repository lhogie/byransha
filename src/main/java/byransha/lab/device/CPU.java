package byransha.lab.device;

import byransha.Element;
import byransha.ID;
import byransha.lab.LabNode;
import byransha.primitive.LongNode;
import byransha.primitive.URLNode;

public class CPU extends LabNode {
	LongNode nbCores;
	URLNode url;

	public CPU(Element parent, ID id) {
		super(parent, id);
	}

}
