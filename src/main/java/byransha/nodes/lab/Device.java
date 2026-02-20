package byransha.nodes.lab;

import byransha.graph.BBGraph;
import byransha.nodes.primitive.StringNode;
import byransha.nodes.system.User;

public class Device extends BusinessNode {
	public StringNode serialNumber;
	public StringNode brand;
	public StringNode modelName;

	public Device(BBGraph g) {
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
	public String prettyName() {
		if(brand != null || serialNumber != null || modelName != null) {
			String pretty = "";
			if(brand != null && brand.get() != null && !brand.get().isBlank()) {
				pretty += brand.get();
			}
			if(modelName != null && modelName.get() != null && !modelName.get().isBlank()) {
				if(!pretty.isBlank()) {
					pretty += " ";
				}
				pretty += modelName.get();
			}
			if(serialNumber != null && serialNumber.get() != null && !serialNumber.get().isBlank()) {
				if (!pretty.isBlank()) {
					pretty += " ";
				}
				pretty += "(" + serialNumber.get() + ")";
			}
			if(!pretty.isBlank()) {
				return pretty;
			}
		}
		return null;
	}
}
