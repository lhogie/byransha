package byransha.nodes.lab;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.nodes.primitive.StringNode;

public class SupportDePoste extends BNode {
	StringNode id;

	protected SupportDePoste(BGraph g) {
		super(g);
		id = new StringNode(g);
	}

	@Override
	public String whatIsThis() {
		return "support de poste";
	}

	@Override
	public String prettyName() {
		return id.prettyName();
	}

}
