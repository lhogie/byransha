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
	public String toString() {
		if (status != null && employer != null) {
			return status + " at " + employer;
		} else if (status != null) {
			return status.toString();
		} else if (employer != null) {
			return "job at " + employer;
		} else {
			return "some position";
		}
	}

	@Override
	public String whatIsThis() {
		return "a position";
	}

}
