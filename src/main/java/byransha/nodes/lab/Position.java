package byransha.nodes.lab;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.nodes.primitive.DateNode;

public class Position extends BNode {
	Structure employer;
	DateNode from;
	DateNode to;
	Status status;
	SupportDePoste support;

	public Position(BGraph g) {
		super(g);
	}

	@Override
	public String prettyName() {
		return status.prettyName() + " at " + employer.prettyName();
	}

	@Override
	public String whatIsThis() {
		return "a position";
	}

}
