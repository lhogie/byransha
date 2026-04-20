package byransha.ai;

import com.fasterxml.jackson.databind.ObjectMapper;


public class QueryIALiveOllamaDemo {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    public static void main(String[] args) throws Exception {
        var inputJson = MAPPER.readTree("""
                {
                  "department": "I3S",
                  "employees": [
                    {"name": "Alice", "age": 30, "hoursPerWeek": 35},
                    {"name": "Bob", "age": 42, "hoursPerWeek": 39},
                    {"name": "Chloe", "age": 25, "hoursPerWeek": 32}
                  ]
                }
                """);

        var question = "Rajoute un employe nommé Pierre, il a 50 ans et il travaille 40 heures par semaine. Retourne uniquement du JSON.";
        var prompt = QueryIA.buildLlmPrompt(inputJson, question, QueryIA.ResponseMode.JSON_ONLY);
        System.out.println("**** Prompt envoye a Ollama ****");
        System.out.println(prompt);

        System.out.println("\n(Appuyez sur 'p' puis 'Entrée' pour interrompre le modèle Ollama en cas de besoin)\n");
        Thread keyListenerThread = new Thread(() -> {
            try {
                while (true) {
                    int c = System.in.read();
                    if (c == 'p' || c == 'P') {
                        System.out.println("\narrêt d'Ollama demandé...");
                        OllamaModel.stopPrimaryModel();
                    }
                }
            } catch (Exception e) {
                // Ignorer l'erreur
            }
        });
        keyListenerThread.setDaemon(true);
        keyListenerThread.start();

        var rawResponse = OllamaModel.chat(prompt);
        System.out.println("\n**** Reponse brute Ollama ****");
        System.out.println(rawResponse);
        var extractedJson = AiResponseAnalyser.extractFirstJsonPayload(rawResponse);
        System.out.println("\n**** JSON extrait (si present) ****");
        System.out.println(extractedJson != null ? extractedJson : "(aucun JSON detecte)");
    }
}