package byransha.lab;

import byransha.ID;
import byransha.graph.Element;
import byransha.lab.device.LocationNode;
import byransha.primitive.StringNode;

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
