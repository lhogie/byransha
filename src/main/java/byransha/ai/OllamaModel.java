package byransha.ai;

import dev.langchain4j.model.ollama.OllamaChatModel;

public class OllamaModel {
	final private static OllamaChatModel instance = OllamaChatModel.builder().baseUrl("http://localhost:11434") // URL
																												// serveur
																												// Ollama
			.modelName("yi-coder:1.5b").temperature(0.2).build();
	
	public static synchronized String chat(String prompt) {
		return instance.chat(prompt);
	}

}