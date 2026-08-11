package byransha.lab.device;

import byransha.ID;
import byransha.graph.Element;
import byransha.graph.LabNode;
import byransha.primitive.LongNode;
import byransha.primitive.StringNode;

public class Disk extends LabNode {
	LongNode rotational;
	StringNode bus = lookupOrCreate("bus", id -> new StringNode(this, id, null, "(nvme)|(sata)"));
	LongNode sizeInGB;

	public Disk(Element parent, ID id) {
		super(parent, id);
	}
}
