package byransha.ai;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.NodeAction;
import byransha.graph.action.ActionResult;
import byransha.graph.action.list.ListNode;
import byransha.nodes.lab.stats.DistributionNode;
import byransha.nodes.primitive.StringNode;
import byransha.nodes.primitive.TextNode;
import byransha.nodes.system.ChatNode;

public class QueryIA extends NodeAction<BNode, BNode> {
	private static final ObjectMapper mapper = new ObjectMapper();
	public final StringNode prompt;
	public final JSONNode inputJSON;

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
	public ActionResult<BNode, BNode> exec(ChatNode chat) throws Throwable {
		var iaResponse = queryIA(inputNode.describeAsJSON(), prompt.get());
		var extractedJson = AiResponseAnalyser.extractFirstJsonPayload(iaResponse);
		var analysableResponse = extractedJson != null ? extractedJson : iaResponse;

		if (AiResponseAnalyser.isArrayOfNumbers(analysableResponse)) {
			JsonNode parsed = mapper.readTree(analysableResponse);
			var numericArrayNode = new ListNode<BNode>(g, "IA numeric array");
			for (JsonNode value : parsed) {
				numericArrayNode.elements.add(new TextNode(g, "value", value.asText()));
			}
			return createResultNode(numericArrayNode, true);
		}

		if (AiResponseAnalyser.isDistribution(analysableResponse)) {
			var distributionNode = new DistributionNode<String>(g) {
				@Override
				public String toString() {
					return "IA distribution";
				}
			};

			JsonNode parsed = mapper.readTree(analysableResponse);
			for (var entry : parsed.properties()) {
				distributionNode.entries.addOccurence(entry.getKey(), entry.getValue().asDouble());
			}

			return createResultNode(distributionNode, true);
		}

		var textNode = new TextNode(g, "IA response", iaResponse);
		textNode.info = true;
		return createResultNode(textNode, true);
	}

		static String buildLlmPrompt(JsonNode inputJSON, String question) {
			return "Given the graph below, answer the user request. If possible, return valid JSON only. Graph: "
					+ inputJSON.toPrettyString() + "\nUser request: " + question;
		}

	protected String queryIA(JsonNode inputJSON, String question) throws JsonMappingException, JsonProcessingException, IOException, InterruptedException, Exception {
			var llmPrompt = buildLlmPrompt(inputJSON, question);
		return OllamaModel.chat(llmPrompt);
	}

}