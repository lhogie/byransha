package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.StringNode;

public class Device extends BusinessNode {
	public StringNode serialNumber;
	public StringNode brand;
	public StringNode modelName;

	public Device(BBGraph g) {
		super(g);
	}

	public Device(BBGraph g, int id) {
		super(g, id);
	}

	@Override
	public String whatIsThis() {
		return "a computer/phone or any physical device";
	}

	@Override
	public String prettyName() {
		if(brand == null || brand.get() == null || brand.get().isEmpty()) {
			System.err.println("Device with no brand: " + this);
			return "Device(unknown)";
		}
		return brand.get() + " " + modelName.get() + "(S/N: " + serialNumber.get() + ")";
	}
}
