package lab;

import byransha.Element;
import byransha.ID;
import byransha.primitive.StringNode;
import lab.device.LocationNode;

public class AddressNode extends Element {
	public final StringNode text = fieldNode("text", id -> new StringNode(this, id, null, ".+"));
	public final LocationNode gpsLocation = fieldNode("gps", id -> new LocationNode(this, id));

	protected AddressNode(Element parent, ID id) {
		super(parent, id);
	}

	@Override
	public String toString() {
		return text.toString();
	}
}
