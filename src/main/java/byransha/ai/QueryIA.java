package byransha.ai;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import byransha.graph.ShowInKishanView;
import byransha.graph.BNode;
import byransha.graph.Category;
import byransha.graph.list.action.FunctionAction;
import byransha.graph.list.action.ListNode;
import byransha.nodes.lab.stats.DistributionNode;
import byransha.nodes.primitive.StringNode;
import byransha.nodes.primitive.TextNode;

public class QueryIA extends FunctionAction<BNode, BNode> {
	private static final ObjectMapper mapper = new ObjectMapper();
	
	@ShowInKishanView
	public final StringNode prompt;
	public final JSONNode inputJSON;

	class AI extends Category {
	}

	public QueryIA(BNode n) {
		super(n, AI.class);
		prompt = new StringNode(this, "", ".+");
		inputJSON = new JSONNode(this, n.describeAsJSON());
	}

	@Override
	public String whatItDoes() {
		return "query an IA";
	}

	@Override
	public boolean applies() {
		return true;
	}

	@Override
	public void impl() throws Throwable {
		var iaResponse = queryIA(inputNode.describeAsJSON(), prompt.get());
		var extractedJson = AiResponseAnalyser.extractFirstJsonPayload(iaResponse);
		var analysableResponse = extractedJson != null ? extractedJson : iaResponse;

		if (AiResponseAnalyser.isArrayOfNumbers(analysableResponse)) {
			JsonNode parsed = mapper.readTree(analysableResponse);
			var l = new ListNode<TextNode>(parent, "IA numeric array", TextNode.class);
			for (JsonNode value : parsed) {
				l.elements.add(new TextNode(this, "value", value.asText()));
			}
			result = l;
		} else if (AiResponseAnalyser.isDistribution(analysableResponse)) {
			var distributionNode = new DistributionNode<String>(this) {
				@Override
				public String toString() {
					return "IA distribution";
				}
			};

			JsonNode parsed = mapper.readTree(analysableResponse);
			for (var entry : parsed.properties()) {
				distributionNode.entries.addOccurence(entry.getKey(), entry.getValue().asDouble());
			}

			result = distributionNode;
		} else {
			result = new TextNode(parent, "IA response", iaResponse);
		}
	}

	static String buildLlmPrompt(JsonNode inputJSON, String question) {
		return "Given the graph below, answer the user request. If possible, return valid JSON only. Graph: "
				+ inputJSON.toPrettyString() + "\nUser request: " + question;
	}

	protected String queryIA(JsonNode inputJSON, String question)
			throws JsonMappingException, JsonProcessingException, IOException, InterruptedException, Exception {
		var llmPrompt = buildLlmPrompt(inputJSON, question);
		return OllamaModel.chat(llmPrompt);
	}

}