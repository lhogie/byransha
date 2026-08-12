package byransha.lab.device;

import byransha.Element;
import byransha.ID;
import byransha.lab.LabElement;
import byransha.primitive.LongNode;
import byransha.primitive.StringNode;

public class Disk extends LabElement {
	LongNode rotational;
	StringNode bus = fieldNode("bus", id -> new StringNode(this, id, null, "(nvme)|(sata)"));
	LongNode sizeInGB;

	public Disk(Element parent, ID id) {
		super(parent, id);
	}
}
