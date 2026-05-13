package byransha.nodes.lab.device;

import byransha.graph.BNode;
import byransha.graph.BusinessNode;
import byransha.graph.NetworkAddressNode;
import byransha.graph.ShowInKishanView;
import byransha.nodes.lab.Room;
import byransha.nodes.lab.Structure;
import byransha.nodes.primitive.LongNode;
import byransha.nodes.primitive.StringNode;
import byransha.nodes.primitive.URLNode;

public class Device extends BusinessNode {
	@ShowInKishanView
	public final StringNode serialNumber = new StringNode(this);
	@ShowInKishanView
	public final StringNode inriaServiceTag = new StringNode(this);
	@ShowInKishanView
	public final MACAddressNode macAddress = new MACAddressNode(this);

	@ShowInKishanView
	public final StringNode dnsName = new StringNode(this);

	@ShowInKishanView
	public final StringNode brand = new StringNode(this);

	@ShowInKishanView
	public final StringNode modelName = new StringNode(this);

	@ShowInKishanView
	public final Structure owner = new Structure(this);

	@ShowInKishanView
	public final Room repository = new Room(this);

	@ShowInKishanView
	public final LongNode financialValue = new LongNode(this);

	@ShowInKishanView
	public final Invoice invoice = new Invoice(this);

	@ShowInKishanView
	public final URLNode productURL = new URLNode(this, null);

	@ShowInKishanView
	public final NetworkAddressNode ip = new NetworkAddressNode(this);

	public Device(BNode parent) {
		super(parent);
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
