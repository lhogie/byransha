package byransha.lab.device;

import byransha.graph.BNode;
import byransha.primitive.LongNode;
import byransha.primitive.StringNode;

public class Disk extends BNode {
	LongNode rotational;
	StringNode bus = new StringNode(this, null, "(nvme)|(sata)");
	LongNode sizeInGB;

	public Disk(BNode parent) {
		super(parent);
	}
}
