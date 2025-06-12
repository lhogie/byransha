package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.BNode;
import byransha.DateNode;

public class Position extends BNode {
	Structure employer;
	DateNode from;
	DateNode to;
	Status status;

	public Position(BBGraph g) {
		super(g);
		employer = BNode.create(g, Structure.class);
		from = BNode.create(g, DateNode.class);
		to = BNode.create(g, DateNode.class);
		status = BNode.create(g, Status.class);
	}

	public Position(BBGraph g, int id) {
		super(g, id);
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
