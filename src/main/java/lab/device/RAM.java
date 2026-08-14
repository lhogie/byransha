package lab.device;

import byransha.Element;
import byransha.ID;
import byransha.primitive.LongNode;
import lab.LabElement;

public class RAM extends LabElement {
	LongNode size;
	LongNode frequency;

	public RAM(Element parent, ID id) {
		super(parent, id);
	}

}
