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

        var question = "Donne une distribution JSON des heures de travail par employe (cle=nom, valeur=heures). Retourne uniquement du JSON.";
        var prompt = QueryIA.buildLlmPrompt(inputJson, question);
        System.out.println("**** Prompt envoye a Ollama ****");
        System.out.println(prompt);
        var rawResponse = OllamaModel.chat(prompt);
        System.out.println("\n**** Reponse brute Ollama ****");
        System.out.println(rawResponse);
        var extractedJson = AiResponseAnalyser.extractFirstJsonPayload(rawResponse);
        System.out.println("\n**** JSON extrait (si present) ****");
        System.out.println(extractedJson != null ? extractedJson : "(aucun JSON detecte)");
    }
}