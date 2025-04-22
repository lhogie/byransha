package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.PersistingNode;

public abstract class BusinessNode extends PersistingNode {

	public BusinessNode(BBGraph g) {
		super(g);
	}

	public BusinessNode(BBGraph g, int id) {
		super(g, id);
	}

}
