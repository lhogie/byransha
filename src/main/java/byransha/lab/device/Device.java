package byransha.lab.device;

import byransha.ID;
import byransha.graph.Element;
import byransha.graph.LabNode;
import byransha.graph.ShowInKishanView;
import byransha.lab.Room;
import byransha.lab.Structure;
import byransha.primitive.LongNode;
import byransha.primitive.NetworkAddressNode;
import byransha.primitive.StringNode;
import byransha.primitive.URLNode;

public class Device extends LabNode {
	@ShowInKishanView
	public final StringNode serialNumber = lookupOrCreate("sn", id -> new StringNode(this, id, null, null));
	@ShowInKishanView
	public final StringNode inriaServiceTag = lookupOrCreate("inriaServiceTag", id -> new StringNode(this, id, null, "[0-9A-Z\\-]+"));
	@ShowInKishanView
	public final MACAddressNode macAddress = lookupOrCreate("mac", id -> new MACAddressNode(this, id));

	@ShowInKishanView
	public final StringNode dnsName = lookupOrCreate("dns", id -> new StringNode(this, id, null, ".+"));

	@ShowInKishanView
	public final StringNode brand = lookupOrCreate("brand", id -> new StringNode(this, id, null, ".+"));

	@ShowInKishanView
	public final StringNode modelName = lookupOrCreate("model", id -> new StringNode(this, id, null, ".+"));

	@ShowInKishanView
	public final Structure owner = lookupOrCreate("owner", id -> null);

	@ShowInKishanView
	public final Room repository = lookupOrCreate("repository", id -> null);

	@ShowInKishanView
	public final LongNode financialValue = lookupOrCreate("financialValue", id -> new LongNode(this, null));

	@ShowInKishanView
	public final Invoice invoice = lookupOrCreate("invoice", id -> new Invoice(this, id));

	@ShowInKishanView
	public final URLNode productURL = lookupOrCreate("productURL", id -> new URLNode(this, id, null));

	@ShowInKishanView
	public final NetworkAddressNode ip = lookupOrCreate("ip", id -> new NetworkAddressNode(this, id));

	public Device(Element parent, ID id) {
		super(parent, id);
	}

	@Override
	public String whatIsThis() {
		return "a computer/phone or any physical device";
	}

	@Override
	public String toString() {
		return firstOf(dnsName, modelName, brand, inriaServiceTag);
	}

	private String firstOf(Object... a) {
		for (var e : a) {
			if (e != null) {
				return e.toString();
			}
		}

		return "undefined";
	}
}
