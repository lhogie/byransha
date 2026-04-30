package byransha.nodes.lab.device;

import byransha.graph.BNode;
import byransha.nodes.primitive.StringNode;

public class MACAddressNode extends StringNode {

	public MACAddressNode(BNode parent) {
		super(parent, null, "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$");
	}
}
