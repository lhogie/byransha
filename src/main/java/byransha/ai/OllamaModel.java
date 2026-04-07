package byransha.ai;

import java.io.IOException;
import java.io.OutputStream;

import static dev.langchain4j.model.chat.Capability.RESPONSE_FORMAT_JSON_SCHEMA;
import dev.langchain4j.model.ollama.OllamaChatModel;


public class OllamaModel {
<<<<<<< HEAD
	final private static OllamaChatModel yi_coder_1_5b_1_2 = OllamaChatModel.builder().baseUrl("http://localhost:11434")
			.modelName("yi-coder:1.5b").temperature(0.2).build();

	public static synchronized String chat(String prompt) {
		return yi_coder_1_5b_1_2.chat(prompt);
	}
=======
	private static final int MAX_RETRIES = 3;
	private static final int RETRY_DELAY_MS = 1000; // 1 second
	private static final String FALLBACK_MODEL = "yi-coder:1.5b"; 
	private static OllamaChatModel fallbackInstance;
	final private static OllamaChatModel instance = OllamaChatModel.builder().baseUrl("http://localhost:11434")
		
		.modelName("granite4:tiny-h").temperature(0.2).supportedCapabilities(RESPONSE_FORMAT_JSON_SCHEMA).logRequests(true).logResponses(true).build();

	
	public static synchronized String chat(String prompt) throws IOException {
    for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
        try {
            System.out.println("[Ollama] Attempt " + attempt + "/" + MAX_RETRIES);
            return instance.chat(prompt);
        } catch (Exception e) {
            System.err.println("[Ollama] Attempt " + attempt + " failed: " + e.getMessage());
            if (attempt < MAX_RETRIES) {
                try { Thread.sleep(RETRY_DELAY_MS); } catch (InterruptedException ie) {}
            }
        }
    }
    // Tous les retries échoués, utiliser un fallback
    System.err.println("[Ollama] All retries failed, using fallback model: " + FALLBACK_MODEL);
	String[] args = new String[] {"/bin/bash", "-c", "ollama stop granite4:tiny-h"};
	Process proc = new ProcessBuilder(args).start();
	OutputStream out;
	out = proc.getOutputStream();  
	out.write("any command".getBytes());  
	out.flush(); 
    return chatWithFallback(prompt);
}

private static String chatWithFallback(String prompt) {
    if (fallbackInstance == null) {
        fallbackInstance = OllamaChatModel.builder()
            .baseUrl("http://localhost:11434")
            .modelName(FALLBACK_MODEL)
            .temperature(0.2)
            .build();
    }
    return fallbackInstance.chat(prompt);
}
>>>>>>> 90854317cd9ca205959cb486ccf1c237a547305e

}