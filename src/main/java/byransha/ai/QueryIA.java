package byransha.ai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.NodeAction;
import byransha.graph.action.ActionResult;
import byransha.nodes.primitive.StringNode;
import byransha.nodes.primitive.TextNode;
import byransha.nodes.system.ChatNode;

public class QueryIA extends NodeAction<BNode, TextNode> {
	public final StringNode prompt;
	public final JSONNode inputJSON;
	OllamaModel ollamaModel = new OllamaModel();

	public QueryIA(BGraph g, BNode n) {
		super(g, n, "IA");
		prompt = new StringNode(g, "", ".+");
		inputJSON = new JSONNode(g, n.describeAsJSON());
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
	public ActionResult<BNode, TextNode> exec(ChatNode chat) throws Throwable {
		var iaResponse = queryIA(inputNode.describeAsJSON(), prompt.get());
		var textNode = new TextNode(g, "IA response", iaResponse);
		textNode.info = true;
		return createResultNode(textNode, true);
	}

	protected String queryIA(JsonNode inputJSON, String question) throws JsonMappingException, JsonProcessingException {
		var prompt = "gives me a JSON array of the IDs of the nodes from the following graph: "
				+ inputJSON.toPrettyString();
		return OllamaModel.chat(prompt);
	}

}