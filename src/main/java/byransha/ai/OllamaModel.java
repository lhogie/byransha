package byransha.ai;

import dev.langchain4j.model.ollama.OllamaChatModel;

public class OllamaModel {
	final private static OllamaChatModel yi_coder_1_5b_1_2 = OllamaChatModel.builder().baseUrl("http://localhost:11434")
			.modelName("yi-coder:1.5b").temperature(0.2).build();

	public static synchronized String chat(String prompt) {
		return yi_coder_1_5b_1_2.chat(prompt);
	}

}