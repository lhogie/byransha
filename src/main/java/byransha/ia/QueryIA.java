package byransha.ia;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.NodeAction;
import byransha.graph.action.ActionResult;
import byransha.nodes.primitive.StringNode;
import byransha.nodes.system.ChatNode;

final public class QueryIA extends NodeAction<BNode, JSONNode> {
	public final StringNode prompt;

	public QueryIA(BGraph g, BNode n) {
		super(g, n);
		prompt = new StringNode(g, "", ".+");
	}

	@Override
	public String whatItDoes() {
		return "query an IA";
	}

	@Override
	public boolean applies(ChatNode chat) {
		return true;
	}

	@Override
	public ActionResult<BNode, JSONNode> exec(ChatNode chat) throws Throwable {
		var iaResponse = queryIA(inputNode.toJSONNode(), prompt.get());
		return createResultNode(new JSONNode(g, iaResponse), true);
	}

	private ObjectNode queryIA(JsonNode inputJSON, String prompt) {
		return null;
	}

}