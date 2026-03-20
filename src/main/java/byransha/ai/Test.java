package byransha.ai;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.jlama.JlamaChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;


public class Test {
    public void initModel() {
        // Afficher la mémoire disponible
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory() / 1024 / 1024; // MB
        System.out.println("Mémoire max disponible : " + maxMemory + " MB\n");

        // Yi-Coder : Modèle spécialisé pour le code et les structures de données
        ChatLanguageModel model = JlamaChatModel.builder()
                .modelName("tjake/Yi-Coder-1.5B-Chat-Jlama")  // Yi-Coder 1.5B optimisé pour les tâches de code et de manipulation de données
                .temperature(0.2f)       // Faible pour du code précis et déterministe
                .maxTokens(1024)         // Suffisant pour générer du JSON
                .build();

        long startTime = System.currentTimeMillis();

        try {
            // Exemple de JSON de base de données graphe (RH)
            String jsonGraph = """
                {
                  "nodes": [
                    {"id": "emp1", "type": "Employee", "name": "Alice Dupont", "role": "Manager"},
                    {"id": "emp2", "type": "Employee", "name": "Bob Martin", "role": "Developer"},
                    {"id": "dept1", "type": "Department", "name": "IT"}
                  ],
                  "edges": [
                    {"from": "emp1", "to": "dept1", "relation": "MANAGES"},
                    {"from": "emp2", "to": "dept1", "relation": "WORKS_IN"}
                  ]
                }
                """;

            // Prompt pour ajouter un nouveau node
            String prompt = String.format("""
                Tu es un assistant qui manipule des bases de données graphes au format JSON.

                Voici la base de données actuelle :
                %s

                Tâche : Ajoute un nouveau employé "Charlie Leblanc" avec le rôle "DevOps Engineer" qui travaille dans le département IT.

                Retourne UNIQUEMENT le JSON complet mis à jour, sans explication.
                """, jsonGraph);

            System.out.println("=== Prompt envoyé ===");
            System.out.println("Longueur du prompt : " + prompt.length() + " caractères");
            System.out.println("Tokens approximatifs : " + (prompt.split("\\s+").length) + "\n");

            String response = model.chat(prompt);

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            System.out.println("\n=== Réponse de l'IA===");
            System.out.println(response);

            System.out.println("\n=== Stats ===");
            System.out.println("Temps de génération : " + duration + " ms");
            System.out.println("Longueur de la réponse : " + response.length() + " caractères");
            System.out.println("Tokens génératifs : " + (response.split("\\s+").length));

            // Vérification que c'est bien du JSON valide
            if (response.trim().startsWith("{") && response.trim().endsWith("}")) {
                System.out.println(" La réponse semble être du JSON valide");
            } else {
                System.out.println(" Attention : La réponse ne ressemble pas à du JSON"); // Pas d'erreur,
                //  juste une alerte pour vérifier la structure de la réponse si il écrit apres c'est normale qu'on a pas de json valide
            }

        } catch (Exception e) {
            System.err.println("ERREUR lors de la génération : " + e.getMessage());
            e.printStackTrace();
        }
    }
    public void OllamaModel() {
         OllamaChatModel ollamaModel = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434") // URL serveur Ollama
                .modelName("yi-coder:1.5b") // Nom du modèle Ollama
                .temperature(0.2)
                .build();

        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory() / 1024 / 1024; // MB
        System.out.println("Mémoire max disponible : " + maxMemory + " MB\n");
        long startTime = System.currentTimeMillis();

        try {
            // Exemple de JSON de base de données graphe (RH)
            String jsonGraph = """
                {
                  "nodes": [
                    {"id": "emp1", "type": "Employee", "name": "Alice Dupont", "role": "Manager"},
                    {"id": "emp2", "type": "Employee", "name": "Bob Martin", "role": "Developer"},
                    {"id": "dept1", "type": "Department", "name": "IT"}
                  ],
                  "edges": [
                    {"from": "emp1", "to": "dept1", "relation": "MANAGES"},
                    {"from": "emp2", "to": "dept1", "relation": "WORKS_IN"}
                  ]
                }
                """;

            // Prompt pour ajouter un nouveau node
            String prompt = String.format("""
                Tu es un assistant qui manipule des bases de données graphes au format JSON.

                Voici la base de données actuelle :
                %s

                Tâche : Combien d'employés nommés "Alice" travaillent dans le département IT ?

                
                """, jsonGraph);

            System.out.println("=== Prompt envoyé ===");
            System.out.println("Longueur du prompt : " + prompt.length() + " caractères");
            System.out.println("Tokens approximatifs : " + (prompt.split("\\s+").length) + "\n");

            String response = ollamaModel.chat(prompt);

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            System.out.println("\n=== Réponse de l'IA===");
            System.out.println(response);

            System.out.println("\n=== Stats ===");
            System.out.println("Temps de génération : " + duration + " ms");
            System.out.println("Longueur de la réponse : " + response.length() + " caractères");
            System.out.println("Tokens génératifs : " + (response.split("\\s+").length));

            // Vérification que c'est bien du JSON valide
            if (response.trim().startsWith("{") && response.trim().endsWith("}")) {
                System.out.println(" La réponse semble être du JSON valide");
            } else {
                System.out.println(" Attention : La réponse ne ressemble pas à du JSON"); // Pas d'erreur,
                //  juste une alerte pour vérifier la structure de la réponse si il écrit apres c'est normale qu'on a pas de json valide
            }

        } catch (Exception e) {
            System.err.println("ERREUR lors de la génération : " + e.getMessage());
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        // Forcer l'encodage UTF-8
        System.setProperty("file.encoding", "UTF-8");
        System.setProperty("sun.stdout.encoding", "UTF-8");
        System.setProperty("sun.stderr.encoding", "UTF-8");

        Test test = new Test();
//        test.initModel();
        test.OllamaModel();
    }
}