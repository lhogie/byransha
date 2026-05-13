package byransha.nodes.lab.device;

import byransha.graph.BNode;
import byransha.nodes.primitive.LongNode;
import byransha.nodes.primitive.StringNode;

public class Disk extends BNode {
	LongNode rotational;
	StringNode bus = new StringNode(this, null, "(nvme)|(sata)");
	LongNode sizeInGB;

	public Disk(BNode parent) {
		super(parent);
	}
}
