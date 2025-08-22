package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.StringNode;
import byransha.User;

public class Device extends BusinessNode {
	public StringNode serialNumber;
	public StringNode brand;
	public StringNode modelName;

	public Device(BBGraph g, User creator, InstantiationInfo ii) {
		super(g,  creator, ii);
		endOfConstructor();
	}

	@Override
	protected void createOuts(User creator) {
		super.createOuts(creator);
		serialNumber = new StringNode(g, creator, InstantiationInfo.persisting);
		brand = new StringNode(g, creator, InstantiationInfo.persisting);
		modelName = new StringNode(g, creator, InstantiationInfo.persisting);
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
