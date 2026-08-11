package byransha.lab.device;

import byransha.Element;
import byransha.ID;
import byransha.lab.LabNode;
import byransha.primitive.LongNode;

public class RAM extends LabNode {
	LongNode size;
	LongNode frequency;

	public RAM(Element parent, ID id) {
		super(parent, id);
	}

}
