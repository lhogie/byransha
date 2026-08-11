package byransha.lab.device;

import byransha.ID;
import byransha.graph.Element;
import byransha.graph.LabNode;
import byransha.primitive.LongNode;

public class RAM extends LabNode {
	LongNode size;
	LongNode frequency;

	public RAM(Element parent, ID id) {
		super(parent, id);
	}

}
