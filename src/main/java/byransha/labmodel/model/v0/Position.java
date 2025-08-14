package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.BNode;
import byransha.DateNode;
import byransha.User;

public class Position extends BNode {
	Structure employer;
	DateNode from;
	DateNode to;
	Status status;

	public Position(BBGraph g, User creator) {
		super(g, creator);
		employer = new Structure(g, creator);
		from = new DateNode(g, creator);
		to = new DateNode(g, creator);
		status = new Status(g, creator);
		endOfConstructor();
	}

	public Position(BBGraph g, User creator, int id) {
		super(g, creator, id);
		endOfConstructor();
	}

	@Override
	public String prettyName() {
		return "position";
	}

	@Override
	public String whatIsThis() {
		return "Position node in the graph";
	}

}
