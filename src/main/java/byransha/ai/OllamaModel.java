
package byransha.ai;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import byransha.util.Cout;

public class OllamaModel {
	private static final int MAX_RETRIES = 3;
	private static final int RETRY_DELAY_MS = 5000; // 5 second
    private static final String PRIMARY_MODEL = "granite4:tiny-h";
    private static final String FALLBACK_MODEL = "yi-coder:1.5b";
    private static volatile ProgressListener globalProgressListener;

    @FunctionalInterface
    public interface ProgressListener {
        void onProgress(ProgressUpdate update);
    }

    public record ProgressUpdate( String message) {
    }

    public static void setGlobalProgressListener(ProgressListener listener) {
        globalProgressListener = listener;
    }

    public static boolean stopPrimaryModel() {
        return stopPrimaryModel(null);
    }

    public static boolean stopPrimaryModel(ProgressListener listener) {
        return stopModel(PRIMARY_MODEL, listener);
    }

    public static boolean stopModel(String modelName) {
        return stopModel(modelName, null);
    }

    public static boolean stopModel(String modelName, ProgressListener listener) {
        emitProgress("Arret de l'application...", listener);
        System.exit(0);
        return true;
    }


	public static synchronized String chat(String prompt) throws IOException, InterruptedException, Exception {
    return chat(prompt, null);
}


    public static synchronized String chat(String prompt, ProgressListener listener) throws IOException, InterruptedException, Exception {
        emitProgress( "Requete IA recue", listener);
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                String response = callModelWithStream(PRIMARY_MODEL, prompt, listener);
                emitProgress( "Requete IA terminee", listener);
                return response;
            } catch (Exception e) {
                emitProgress( "Echec tentative " + attempt + ": " + sanitizeErrorMessage(e), listener);
                if (attempt < MAX_RETRIES) {
                    sleepBeforeRetry();
                }
            }
        }
        stopModel(PRIMARY_MODEL, listener);
        String fallbackResponse = callModelWithStream(FALLBACK_MODEL, prompt, listener);
        emitProgress( "Requete IA terminee via fallback", listener);
        return fallbackResponse;
    }

    private static String callModelWithStream(String modelName, String prompt, ProgressListener listener) throws Exception {
        emitProgress("Connexion a Ollama en streaming", listener);
        
        ObjectMapper mapper = new ObjectMapper();
        var payload = java.util.Map.of(
            "model", modelName,
            "stream", true,
            "messages", java.util.List.of(java.util.Map.of("role", "user", "content", prompt))
        );

        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:11434/api/chat"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(payload)))
                .build();

        var response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofLines());
        
        StringBuilder content = new StringBuilder(), thinking = new StringBuilder();
        boolean[] inThinking = {false}; // Array 1 case pour modifier dans un lambda/stream
        
        response.body().filter(line -> !line.isEmpty()).forEach(line -> {
            try {
                JsonNode msg = mapper.readTree(line).path("message");
                String t = msg.path("thinking").asText("");
                String c = msg.path("content").asText("");
                
                if (!t.isEmpty()) {
                    if (!inThinking[0]) { inThinking[0] = true; System.out.print("Thinking:\n"); }
                    System.out.print(t);
                    thinking.append(t);
                } else if (!c.isEmpty()) {
                    if (inThinking[0]) { inThinking[0] = false; System.out.print("\n\nAnswer:\n"); }
                    System.out.print(c);
                    content.append(c);
                }
                System.out.flush();
            } catch (Exception e) {}
        });
        
        System.out.println(); // Final newline
        emitProgress("Finalisation de la reponse", listener);
        return content.toString();
    }

    private static void sleepBeforeRetry() {
        try {
            Thread.sleep(RETRY_DELAY_MS);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }


    private static String sanitizeErrorMessage(Exception e) {
        String msg = e.getMessage();
        return msg == null || msg.isBlank() ? e.getClass().getSimpleName() : msg;
    }


    private static void emitProgress( String message,ProgressListener localListener) {
        ProgressUpdate update = new ProgressUpdate(message);
        Cout.progress( message);
        notifyListener(localListener, update);
        notifyListener(globalProgressListener, update);
    }

    
    private static void notifyListener(ProgressListener listener, ProgressUpdate update) {
        if (listener == null) {
            return;
        }
        try {
            listener.onProgress(update);
        } catch (Exception listenerException) {
            Cout.warning("Echec listener progression IA: " + sanitizeErrorMessage(listenerException));
        }
    }

}