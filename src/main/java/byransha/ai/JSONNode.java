package byransha.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import byransha.graph.BGraph;
import byransha.graph.BNode;

final public class JSONNode extends BNode {
	private final JsonNode node;

	public JSONNode(BGraph g, JsonNode n) {
		super(g);
		this.node = n;
	}

	@Override
	public String whatIsThis() {
		return "JSON data";
	}

	@Override
	public ObjectNode describeAsJSON() {
		ObjectNode n = new ObjectNode(factory);
		n.set("json", node);
		return n;
	}

	@Override
	public String prettyName() {
		return "some JSON data";
	}

}