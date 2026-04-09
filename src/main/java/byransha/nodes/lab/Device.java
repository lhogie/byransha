package byransha.nodes.lab;

import byransha.graph.BGraph;
import byransha.graph.NetworkAddressNode;
import byransha.nodes.primitive.StringNode;

public class Device extends BusinessNode {
	public StringNode serialNumber;
	public StringNode brand;
	public StringNode modelName;
	public NetworkAddressNode ip;

	public Device(BGraph g) {
		super(g);
		serialNumber = new StringNode(g);
		brand = new StringNode(g);
		modelName = new StringNode(g);
	}

	@Override
	public String whatIsThis() {
		return "a computer/phone or any physical device";
	}

	@Override
	public String toString() {
		if (brand != null || serialNumber != null || modelName != null) {
			String pretty = "";
			if (brand != null && brand.get() != null && !brand.get().isBlank()) {
				pretty += brand.get();
			}
			if (modelName != null && modelName.get() != null && !modelName.get().isBlank()) {
				if (!pretty.isBlank()) {
					pretty += " ";
				}
				pretty += modelName.get();
			}
			if (serialNumber != null && serialNumber.get() != null && !serialNumber.get().isBlank()) {
				if (!pretty.isBlank()) {
					pretty += " ";
				}
				pretty += "(" + serialNumber.get() + ")";
			}
			if (!pretty.isBlank()) {
				return pretty;
			}
		}
		return null;
	}
}
