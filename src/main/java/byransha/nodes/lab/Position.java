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
		if (status != null && employer != null) {
			return status.prettyName() + " at " + employer.prettyName();
		} else if (status != null) {
			return status.prettyName();
		} else if (employer != null) {
			return "job at " + employer.prettyName();
		} else {
			return "some position";
		}
	}

	@Override
	public String whatIsThis() {
		return "a position";
	}

}
