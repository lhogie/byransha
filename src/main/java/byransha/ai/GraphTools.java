package byransha.ai;

import byransha.graph.BNode;
import byransha.graph.action.search.Search;
import byransha.graph.action.search.SearchRegexp;
import byransha.graph.action.search.SearchText;
import dev.langchain4j.agent.tool.Tool;

public class GraphTools {
    private final BNode contextNode;
    
    public GraphTools(BNode contextNode) {
        this.contextNode = contextNode;
    }
    
    @Tool("Recherche tous les nœuds dans le graphe jusqu'à une profondeur donnée. Utilise un parcours en largeur (BFS).")
    public String searchByDepth(int maxDepth) {
        if (maxDepth < 0 || maxDepth > 20) {
            return "Erreur: la profondeur doit être entre 0 et 20";
        }
        try {
            var search = new Search(contextNode);
            search.depth.set((long) maxDepth);
            search.impl();
            
            var result = search.result;
            if (result == null || result.elements.isEmpty()) {
                return "Aucun nœud trouvé à cette profondeur";
            }
            var response = new StringBuilder();
            response.append(String.format("Trouvé %d nœud(s) à profondeur %d:\n", 
                result.elements.size(), maxDepth));
            
            // Limiter à 10 résultats pour ne pas surcharger le contexte
            int count = 0;
            for (var node : result.elements) {
                if (count >= 10) {
                    response.append(String.format("... et %d autres résultats\n", 
                        result.elements.size() - 10));
                    break;
                }
                if (node instanceof BNode bnode) {
                    response.append(String.format("- [%s] %s: %s\n",
                        bnode.idAsText(),
                        bnode.getClass().getSimpleName(),
                        bnode.toString()));
                }
                count++;
            } 
            return response.toString();
        } catch (Exception e) {
            return "Erreur lors de la recherche: " + e.getMessage();
        }
    }
    
    @Tool("Recherche des nœuds contenant un texte spécifique dans le graphe")
    public String searchByText(String searchText, int maxDepth) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return "Erreur: le texte de recherche ne peut pas être vide";
        }
        if (maxDepth < 0 || maxDepth > 20) {
            maxDepth = 5; // Valeur par défaut
        }
        try {
            var search = new SearchText(contextNode);
            search.depth.set((long) maxDepth);
            search.impl();
            var result = search.result;
            if (result == null || result.elements.isEmpty()) {
                return String.format("Aucun nœud trouvé contenant '%s'", searchText);
            }
            var response = new StringBuilder();
            response.append(String.format("Trouvé %d nœud(s) contenant '%s':\n", 
                result.elements.size(), searchText));
            int count = 0;
            for (var node : result.elements) {
                if (count >= 10) {
                    response.append(String.format("... et %d autres résultats\n", 
                        result.elements.size() - 10));
                    break;
                }
                if (node instanceof BNode bnode) {
                    response.append(String.format("- [%s] %s: %s\n",
                        bnode.idAsText(),
                        bnode.getClass().getSimpleName(),
                        bnode.toString()));
                }
                count++;
            }
            return response.toString();
        } catch (Exception e) {
            return "Erreur lors de la recherche textuelle: " + e.getMessage();
        }
    }
    
    @Tool("Recherche des nœuds correspondant à une expression régulière dans le graphe")
    public String searchByRegex(String pattern, int maxDepth) {
        if (pattern == null || pattern.trim().isEmpty()) {
            return "Erreur: le pattern regex ne peut pas être vide";
        }
        
        if (maxDepth < 0 || maxDepth > 20) {
            maxDepth = 5;
        }
        try {
            var search = new SearchRegexp(contextNode);
            search.depth.set((long) maxDepth);
            search.impl();
            var result = search.result;
            if (result == null || result.elements.isEmpty()) {
                return String.format("Aucun nœud trouvé correspondant au pattern '%s'", pattern);
            }
            var response = new StringBuilder();
            response.append(String.format("Trouvé %d nœud(s) correspondant au pattern '%s':\n", 
                result.elements.size(), pattern));
            int count = 0;
            for (var node : result.elements) {
                if (count >= 10) {
                    response.append(String.format("... et %d autres résultats\n", 
                        result.elements.size() - 10));
                    break;
                }
                if (node instanceof BNode bnode) {
                    response.append(String.format("- [%s] %s: %s\n",
                        bnode.idAsText(),
                        bnode.getClass().getSimpleName(),
                        bnode.toString()));
                }
                count++;
            }
            return response.toString();
        } catch (Exception e) {
            return "Erreur lors de la recherche regex: " + e.getMessage();
        }
    }
    
    @Tool("Obtient une description détaillée d'un nœud spécifique par son ID")
    public String getNodeDetails(String nodeId) {
        if (nodeId == null || nodeId.trim().isEmpty()) {
            return "Erreur: l'ID du nœud ne peut pas être vide";
        }
        try {
            // Chercher le nœud en parcourant le graphe
            var searchResult = new java.util.ArrayList<BNode>();
            var visited = new java.util.HashSet<Long>();
            
            java.util.function.Consumer<BNode> traverse = new java.util.function.Consumer<BNode>() {
                @Override
                public void accept(BNode node) {
                    if (node == null || visited.contains(node.id())) return;
                    visited.add(node.id());
                    
                    if (nodeId.equals(node.idAsText())) {
                        searchResult.add(node);
                    }
                    
                    // Parcourir les enfants
                    node.forEachOut((out, role) -> this.accept(out));
                }
            };
            traverse.accept(contextNode);
            if (searchResult.isEmpty()) {
                return String.format("Aucun nœud trouvé avec l'ID '%s'", nodeId);
            }
            var node = searchResult.get(0);
            var response = new StringBuilder();
            response.append(String.format("Détails du nœud [%s]:\n", nodeId));
            response.append(String.format("Type: %s\n", node.getClass().getSimpleName()));
            response.append(String.format("Description: %s\n", node.whatIsThis()));
            response.append(String.format("Valeur: %s\n", node.toString()));
            response.append(String.format("Profondeur: %d\n", node.depth()));
            response.append(String.format("Lecture seule: %s\n", node.isReadOnly() ? "Oui" : "Non"));
            // Lister les relations sortantes
            var outs = new java.util.ArrayList<String>();
            node.forEachOut((out, role) -> {
                if (out != null) {
                    outs.add(String.format("%s -> [%s]", role, out.idAsText()));
                }
            });
            if (!outs.isEmpty()) {
                response.append("\nRelations sortantes:\n");
                outs.forEach(s -> response.append("  - ").append(s).append("\n"));
            }
            return response.toString();
            
        } catch (Exception e) {
            return "Erreur lors de la récupération des détails: " + e.getMessage();
        }
    }
}