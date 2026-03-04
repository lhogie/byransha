package byransha.nodes.lab;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.nodes.primitive.DateNode;
import byransha.nodes.system.User;

public class Position extends BNode {
	Structure employer;
	DateNode from;
	DateNode to;
	Status status;

	public Position(BGraph g) {
		super(g);
	}

	@Override
	public String prettyName() {
		if(status != null) return "Position with status: " + status.prettyName();
		return null;
	}

	@Override
	public String whatIsThis() {
		return "Position node in the graph";
	}

}
