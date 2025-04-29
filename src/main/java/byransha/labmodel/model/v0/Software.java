package byransha.labmodel.model.v0;

import byransha.BooleanNode;
import byransha.BBGraph;

public class Software extends Publication {
	public Software(BBGraph g) {
		super(g);
		openSource = (BooleanNode) g.addNode(BooleanNode.class); //new BooleanNode(g);
		// TODO Auto-generated constructor stub
	}

	public Software(BBGraph g, int id) {
		super(g, id);
		// TODO Auto-generated constructor stub
	}

	BooleanNode openSource;

}
