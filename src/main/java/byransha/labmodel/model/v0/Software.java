package byransha.labmodel.model.v0;

import byransha.BooleanNode;
import byransha.BBGraph;
import byransha.BNode;
import byransha.User;

public class Software extends Publication {
	public Software(BBGraph g, User creator) {
		super(g, creator);
		openSource = new BooleanNode(g, creator); //new BooleanNode(g);
	}

	public Software(BBGraph g, User creator, int id) {
		super(g, creator, id);
	}

	BooleanNode openSource;

}
