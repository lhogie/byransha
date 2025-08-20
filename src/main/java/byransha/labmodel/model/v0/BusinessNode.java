package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.NotPrimitiveNode;
import byransha.User;

public abstract class BusinessNode extends NotPrimitiveNode {

	public BusinessNode(BBGraph g, User creator, InstantiationInfo ii) {
		super(g, creator, ii);
		endOfConstructor();
	}


	@Override
	public String toString() {
		return prettyName();
	}
}
