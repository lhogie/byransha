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
		employer = g.create(  Structure.class);
		from = g.create( DateNode.class);
		to = g.create(  DateNode.class);
		status = g.create(  Status.class);
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
