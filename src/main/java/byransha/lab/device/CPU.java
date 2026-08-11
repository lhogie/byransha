package byransha.lab.device;

import byransha.ID;
import byransha.graph.Element;
import byransha.graph.LabNode;
import byransha.primitive.LongNode;
import byransha.primitive.URLNode;

public class CPU extends LabNode {
	LongNode nbCores;
	URLNode url;

	public CPU(Element parent, ID id) {
		super(parent, id);
	}

}
