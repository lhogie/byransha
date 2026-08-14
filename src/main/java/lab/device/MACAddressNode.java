package lab.device;

import byransha.Element;
import byransha.ID;
import byransha.primitive.StringNode;

public class MACAddressNode extends StringNode {

	public MACAddressNode(Element parent, ID id) {
		super(parent, id, null, "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$");
	}
}
