package byransha.labmodel.model.v0;

import byransha.BooleanNode;
import byransha.BBGraph;
import byransha.BNode;

public class Software extends Publication {
	public Software(BBGraph g) {
		super(g);
		openSource = g.create( BooleanNode.class); //new BooleanNode(g);
	}

	public Software(BBGraph g, int id) {
		super(g, id);
	}

	BooleanNode openSource;

}
