package byransha.ai;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import byransha.graph.ActionParameter;
import byransha.graph.BNode;
import byransha.graph.Category;
import byransha.graph.list.action.FunctionAction;
import byransha.graph.list.action.ListNode;
import byransha.nodes.lab.stats.DistributionNode;
import byransha.nodes.primitive.StringNode;
import byransha.nodes.primitive.TextNode;

public class QueryIA extends FunctionAction<BNode, BNode> {
	private static final ObjectMapper mapper = new ObjectMapper();

	public enum ResponseMode {
		JSON_ONLY,
		TEXT_PLUS_JSON
	}

	@ActionParameter()
	public final StringNode prompt;
	public final JSONNode inputJSON;
	private volatile ResponseMode responseMode = ResponseMode.JSON_ONLY;

	class AI extends Category {
	}
	
	public QueryIA(BNode n) {
		super(n, AI.class);
		prompt = new StringNode(g, "", ".+");
		inputJSON = new JSONNode(g, n.describeAsJSON());
	}

	@Override
	public String whatItDoes() {
		return "Demander a l'ia";
	}

	@Override
	public boolean applies() {
		return true;
	}

	@Override
	public void impl() throws Throwable {
		var focusedNodeJson = inputNode.describeAsJSON();
		var userQuestion = prompt.get();
		if (userQuestion == null || userQuestion.trim().isEmpty()) {
			result = new TextNode(g, "IA response",
					"Erreur: la question envoyee a l'IA est vide. Saisissez une instruction (ex: remplace la cle offices par bureau)."
			);
			return;
		}

		var iaResponse = queryIA(focusedNodeJson, userQuestion);

		if (responseMode == ResponseMode.TEXT_PLUS_JSON) {
			result = new TextNode(g, "IA response", iaResponse);
			return;
		}

		var extractedJson = AiResponseAnalyser.extractFirstJsonPayload(iaResponse);
		var analysableResponse = extractedJson != null ? extractedJson : iaResponse;

		if (AiResponseAnalyser.isArrayOfNumbers(analysableResponse)) {
			JsonNode parsed = mapper.readTree(analysableResponse);
			var l = new ListNode<BNode>(g, "IA numeric array");
			for (JsonNode value : parsed) {
				l.elements.add(new TextNode(g, "value", value.asText()));
			}
			result = l;
		} else if (AiResponseAnalyser.isDistribution(analysableResponse)) {
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

			result = distributionNode;
		} else {
			result = new TextNode(g, "IA response", iaResponse);

		}
	}

	public void setResponseMode(ResponseMode responseMode) {
		this.responseMode = responseMode == null ? ResponseMode.JSON_ONLY : responseMode;
	}

	public ResponseMode getResponseMode() {
		return responseMode;
	}

	static String buildLlmPrompt(JsonNode inputJSON, String question, ResponseMode mode) {
		var normalizedQuestion = question == null ? "" : question.trim();
		var prompt = new StringBuilder();
		prompt.append("Your personality: You are a helpful assistant.\n");
		prompt.append("You are an expert JSON transformation system.\n");
		prompt.append("Your task is to take the original JSON and create an updated JSON by following the user's instructions.\n");
		prompt.append("The user's instructions might be in French (e.g. 'remplace ... par ...', 'ajoute ...'). You must understand and execute these modifications correctly on the JSON keys and values.\n");
		prompt.append("If the user asks to replace one name by another in the JSON, interpret it as a rename of the JSON key or value, not as a free-form rewrite. Example: 'remplace le nom offices par bureau' means rename the key 'offices' to 'bureau' inside the JSON, and keep every other field unchanged.\n");
		prompt.append("All fields that are not targeted by the request must be kept EXACTLY as they are in the original JSON. Do not invent new fields and do not change unrelated field names or values.\n\n");
		
		prompt.append("--- USER INSTRUCTIONS ---\n");
		prompt.append(normalizedQuestion).append("\n\n");

		prompt.append("--- ORIGINAL JSON ---\n");
		prompt.append(inputJSON.toPrettyString()).append("\n\n");

		prompt.append("--- FINAL OUTPUT REQUIREMENT ---\n");
		if (mode == ResponseMode.TEXT_PLUS_JSON) {
			prompt.append("Provide a short explanation.\n");
		} else {
			prompt.append("Output STRICTLY valid JSON ONLY. Do NOT output any intro text, summary, or markdown formatting like ```json. Your entire response must be parseable by a JSON parser.\n");
		}
		System.out.println("**** Prompt construit pour l'IA ****");
		System.out.println(prompt);
		return prompt.toString();
	}

	protected String queryIA(JsonNode inputJSON, String question)
			throws JsonMappingException, JsonProcessingException, IOException, InterruptedException, Exception {
		var llmPrompt = buildLlmPrompt(inputJSON, question, responseMode);
		return OllamaModel.chat(llmPrompt, null, this::handleIAResponseChunk);
	}

}