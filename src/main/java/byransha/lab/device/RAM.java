package byransha.lab.device;

import byransha.Element;
import byransha.ID;
import byransha.lab.LabElement;
import byransha.primitive.LongNode;

public class RAM extends LabElement {
	LongNode size;
	LongNode frequency;

	public RAM(Element parent, ID id) {
		super(parent, id);
	}

}
