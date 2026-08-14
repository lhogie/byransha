package lab.device;

import byransha.Element;
import byransha.ID;
import byransha.action.base.ShowInKishanView;
import byransha.primitive.LongNode;
import byransha.primitive.NetworkAddressNode;
import byransha.primitive.StringNode;
import byransha.primitive.URLNode;
import lab.LabElement;
import lab.Room;
import lab.Structure;

public class Device extends LabElement {
	@ShowInKishanView
	public final StringNode serialNumber = fieldNode("sn", id -> new StringNode(this, id, null, null));
	@ShowInKishanView
	public final StringNode inriaServiceTag = fieldNode("inriaServiceTag",
			id -> new StringNode(this, id, null, "[0-9A-Z\\-]+"));
	@ShowInKishanView
	public final MACAddressNode macAddress = fieldNode("mac", id -> new MACAddressNode(this, id));

	@ShowInKishanView
	public final StringNode dnsName = fieldNode("dns", id -> new StringNode(this, id, null, ".+"));

	@ShowInKishanView
	public final StringNode brand = fieldNode("brand", id -> new StringNode(this, id, null, ".+"));

	@ShowInKishanView
	public final StringNode modelName = fieldNode("model", id -> new StringNode(this, id, null, ".+"));

	@ShowInKishanView
	public final Structure owner = fieldNode("owner", id -> null);

	@ShowInKishanView
	public final Room repository = fieldNode("repository", id -> null);

	@ShowInKishanView
	public final LongNode financialValue = fieldNode("financialValue", id -> new LongNode(this, null));

	@ShowInKishanView
	public final Invoice invoice = fieldNode("invoice", id -> new Invoice(this, id));

	@ShowInKishanView
	public final URLNode productURL = fieldNode("productURL", id -> new URLNode(this, id, null));

	@ShowInKishanView
	public final NetworkAddressNode ip = fieldNode("ip", id -> new NetworkAddressNode(this, id));

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
