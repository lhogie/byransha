package byransha.ia;

import com.fasterxml.jackson.databind.node.ObjectNode;

import byransha.graph.BGraph;
import byransha.graph.BNode;

final public class JSONNode extends BNode {
	private final ObjectNode node;

	public JSONNode(BGraph g, ObjectNode n) {
		super(g);
		this.node = n;
	}

	@Override
	public String whatIsThis() {
		return "JSON data";
	}

	@Override
	public ObjectNode toJSONNode() {
		return node;
	}

	@Override
	public String prettyName() {
		return "some JSON data";
	}

}