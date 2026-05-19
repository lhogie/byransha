package byransha.ai;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import byransha.ai.QueryIA.AI;
import byransha.ai.QueryIA.ResponseMode;
import byransha.ai.QueryIA.ToolEnabledAssistant;
import byransha.graph.BNode;
import byransha.graph.Category;
import byransha.graph.ShowInKishanView;
import byransha.graph.list.action.FunctionAction;
import byransha.graph.list.action.ListNode;
import byransha.nodes.lab.stats.DistributionNode;
import byransha.nodes.primitive.BooleanNode;
import byransha.nodes.primitive.StringNode;
import byransha.nodes.primitive.TextNode;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.store.memory.chat.InMemoryChatMemoryStore;



public class QueryIA extends FunctionAction<BNode, BNode> {
	private static final ObjectMapper mapper = new ObjectMapper();
	private static final ConcurrentHashMap<String, OllamaStreamingChatModel> MODEL_CACHE = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, ToolEnabledAssistant> ASSISTANT_CACHE = new ConcurrentHashMap<>();
	private static final InMemoryChatMemoryStore MEMORY_STORE = new InMemoryChatMemoryStore();
    private static final int MAX_MESSAGES = 10;

	public enum ResponseMode {
		JSON_ONLY, CONVERSATION
	}
	
	public enum Temerature {
		LOW,MEDIUM
	}

	@ShowInKishanView
	public final StringNode prompt = new StringNode(this, "", ".+");;
	public final JSONNode inputJSON ;
	@ShowInKishanView
	public final BooleanNode useGraphTools = new BooleanNode(this, false);
	private static final String PRIMARY_MODEL = "granite4:tiny-h";
	
	public final String ollamaBaseUrl = "http://localhost:11434";
	private volatile ResponseMode responseMode = ResponseMode.JSON_ONLY;

	interface ToolEnabledAssistant {
		@SystemMessage("{{system}}")
		TokenStream chat(@V("system") String systemMessage, @UserMessage String userMessage);
	}
	

	class AI extends Category {
	}

	public QueryIA(BNode n) {
		super(n, AI.class);
		inputJSON = new JSONNode(this, n.describeAsJSON());
	}

	@Override
	public String whatItDoes() {
		return "ask AI";
	}

	@Override
	public boolean applies() {
		return true;
	}



	@Override
	public void impl() throws Throwable {
        var userQuestion = prompt.get();
        if (userQuestion == null || userQuestion.trim().isEmpty()) {
            result = new TextNode(g(), "IA response",
                "Erreur: la question envoyée à l'IA est vide.");
            return;
        }

        String iaResponse;
    
            var focusedNodeJson = inputNode.describeAsJSON();
            iaResponse = queryIA(focusedNodeJson, userQuestion);
        
        
        // Traiter la réponse
        if (responseMode == ResponseMode.CONVERSATION) {
            result = new TextNode(g(), "IA response", iaResponse);
            return;
        }

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

	public void setResponseMode(ResponseMode responseMode) {
		this.responseMode = responseMode == null ? ResponseMode.JSON_ONLY : responseMode;
	}


	public ResponseMode getResponseMode() {
		return responseMode;
	}

	static String[] buildLlmPrompt(JsonNode inputJSON, String question, ResponseMode mode) {
		var normalizedQuestion = question == null ? "" : question.trim();
		var SystemPrompt = new StringBuilder();
		
		if (mode == ResponseMode.JSON_ONLY) {
			SystemPrompt.append("You have access to the graph tools. Before producing the output, call the necessary tools to gather evidence. After using the tools, provide STRICTLY valid JSON that meets the requirements below.\n");
		} else {
			SystemPrompt.append("Tu peux utiliser les outils du graphe si nécessaire pour répondre. Donne une réponse conversationnelle courte et claire.\n");
		}
		
		SystemPrompt.append("Your personality: You are a helpful assistant.\n");
		SystemPrompt.append("You are an expert JSON transformation system.\n");
		SystemPrompt.append(
				"Your task is to take the original JSON and create an updated JSON by following the user's instructions.\n");
		SystemPrompt.append(
				"The user's instructions might be in French (e.g. 'remplace ... par ...', 'ajoute ...'). You must understand and execute these modifications correctly on the JSON keys and values.\n");
		SystemPrompt.append(
				"If the user asks to replace one name by another in the JSON, interpret it as a rename of the JSON key or value, not as a free-form rewrite. Example: 'remplace le nom offices par bureau' means rename the key 'offices' to 'bureau' inside the JSON, and keep every other field unchanged.\n");
		SystemPrompt.append(
				"All fields that are not targeted by the request must be kept EXACTLY as they are in the original JSON. Do not invent new fields and do not change unrelated field names or values.\n\n");
		
		var UserPrompt = new StringBuilder();
		UserPrompt.append("--- USER INSTRUCTIONS ---\n");
		UserPrompt.append(normalizedQuestion).append("\n\n");

		// Au lieu d'envoyer tout le JSON et saturer la mémoire, on donne juste une mini-info
		UserPrompt.append("--- CONTEXT ---\n");
		UserPrompt.append("Focused Node ID: ").append(inputJSON.has("id") ? inputJSON.get("id").asText() : "unknown").append("\n");
		UserPrompt.append("If you need to know the properties, children, or content of this node, YOU MUST use your GraphTools (searchByText, getNodeDetails, etc.). Do not guess.\n\n");

		SystemPrompt.append("--- FINAL OUTPUT REQUIREMENT ---\n");
		if (mode == ResponseMode.CONVERSATION) {
			SystemPrompt.append("Provide a short explanation.\n");
		} else {
			SystemPrompt.append(
					"Output STRICTLY valid JSON ONLY. Do NOT output any intro text, summary, or markdown formatting like ```json. Your entire response must be parseable by a JSON parser.\n");
		}
		
		return new String[] { SystemPrompt.toString(), UserPrompt.toString() };
	}

	protected String queryIA(JsonNode inputJSON, String question)
			throws Exception {
		
		var assistant = getOrCreateAssistant();
		var prompts = buildLlmPrompt(inputJSON, question, responseMode);

		// Synchronous fallback wrapper since `impl()` doesn't support async streams yet.
		// Future dev: Link `assistant.chat(...)` .onNext directly to `TextNode` real-time UI.
		java.util.concurrent.CompletableFuture<String> future = new java.util.concurrent.CompletableFuture<>();
		
		assistant.chat(prompts[0], prompts[1])
			.onNext(token -> {
				// Temporary: print to console to prove stream is working
				System.out.print(token);
			})
			.onComplete(response -> {
				System.out.println(); // newline after stream
				future.complete(response.content().text());
			})
			.onError(future::completeExceptionally)
			.start();

		return future.join();
	}


 private ToolEnabledAssistant getOrCreateAssistant() {
        var cacheKey = ollamaBaseUrl + "|" + PRIMARY_MODEL;
        
        return ASSISTANT_CACHE.computeIfAbsent(cacheKey, key -> {
            var model = getOrCreateModel();

			ChatMemory memory = MessageWindowChatMemory.builder()
				.maxMessages(MAX_MESSAGES)
				.chatMemoryStore(MEMORY_STORE)
				.build();
            
            return AiServices.builder(ToolEnabledAssistant.class)
                    .streamingChatLanguageModel(model)
                    .tools(new GraphTools(inputNode))
                    .chatMemory(memory)
                    .build();
        });
    }
	private OllamaStreamingChatModel getOrCreateModel() {
        var cacheKey = ollamaBaseUrl + "|" + PRIMARY_MODEL;
        
        return MODEL_CACHE.computeIfAbsent(cacheKey, key -> 
            OllamaStreamingChatModel.builder()
                .baseUrl(ollamaBaseUrl)
                .modelName(PRIMARY_MODEL)
                .numCtx(8192)
                .temperature((double) OllamaModel.LOW)
                .timeout(java.time.Duration.ofMinutes(5))
                .logRequests(false)   // Mettre à true pour déboguer
                .logResponses(false)  // Mettre à true pour déboguer
                .build()
        );
    }
}
