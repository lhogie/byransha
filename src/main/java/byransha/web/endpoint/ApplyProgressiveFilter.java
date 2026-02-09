package byransha.web.endpoint;

import byransha.*;
import byransha.filter.FilterChain;
import byransha.filter.FilterResultPair;
import byransha.labmodel.model.v0.BusinessNode;
import byransha.web.EndpointJsonResponse;
import byransha.web.NodeEndpoint;
import byransha.web.WebServer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpsExchange;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Endpoint pour appliquer un filtre progressif spécifique et capturer ses résultats
 */
public class ApplyProgressiveFilter extends NodeEndpoint<BNode> {
    
    public ApplyProgressiveFilter(BBGraph g) {
        super(g);
        endOfConstructor();
    }

    @Override
    public String whatItDoes() {
        return "Applique un filtre progressif spécifique et capture ses résultats de manière isolée";
    }

    @Override
    public EndpointJsonResponse exec(
        ObjectNode in,
        User user,
        WebServer webServer,
        HttpsExchange exchange,
        BNode currentNode
    ) throws Throwable {
        
        // Récupérer l'ID de la paire FilterResultPair via requireParm
        String pairIdStr = requireParm(in, "pairId").asText();
        
        long pairId = Long.parseLong(pairIdStr);
        BNode pairNode = g.findByID((int) pairId);
        
        if (!(pairNode instanceof FilterResultPair)) {
            return new EndpointJsonResponse(
                new ObjectNode(null),
                "FilterResultPair non trouvé avec ID: " + pairId
            );
        }
        
        FilterResultPair pair = (FilterResultPair) pairNode;
        
        // Trouver le filtre précédent pour affinage progressif
        FilterResultPair previousPair = findPreviousPair(currentNode, pair);
        
        // Appliquer les filtres et capturer les résultats
        FilterChain activeFilterChain = pair.filterChain;
        final String searchTerm;
        
        if (currentNode instanceof SearchForm sf) {
            String term = sf.searchTerm.get();
            searchTerm = (term != null) ? term : "";
        } else {
            searchTerm = "";
        }
        
        // Récupérer les nœuds de départ : 
        // - Si c'est le premier filtre : chercher dans tout le graphe
        // - Sinon : partir des résultats du filtre précédent
        List<BusinessNode> nodes;
        
        if (previousPair == null) {
            // Premier filtre : rechercher dans tout le graphe
            nodes = g.findAll(BusinessNode.class, node -> {
                if (node.getClass().getSimpleName().equals("SearchForm")) {
                    return false;
                }
                if (node.getClass().getSimpleName().equals("ShortcutNode")) {
                    return false;
                }
                
                if (!searchTerm.isEmpty()) {
                    return basicSearch(node, searchTerm);
                }
                return true;
            });
        } else {
            // Filtres suivants : partir des résultats du filtre précédent
            nodes = new ArrayList<>();
            for (BNode node : previousPair.results.getElements()) {
                if (node instanceof BusinessNode bn) {
                    if (!searchTerm.isEmpty()) {
                        if (basicSearch(bn, searchTerm)) {
                            nodes.add(bn);
                        }
                    } else {
                        nodes.add(bn);
                    }
                }
            }
        }
        
        // Appliquer la FilterChain de cette paire
        if (activeFilterChain != null && activeFilterChain.hasFilledValues()) {
            nodes = nodes.stream()
                    .filter(activeFilterChain.toPredicate())
                    .collect(Collectors.toList());
        }
        
        // Dédupliquer
        Map<String, BusinessNode> deduplicatedMap = new LinkedHashMap<>();
        for (BusinessNode node : nodes) {
            String deduplicationKey = node.prettyName() + "|" + node.getClass().getSimpleName();
            deduplicatedMap.putIfAbsent(deduplicationKey, node);
        }
        nodes = new ArrayList<>(deduplicatedMap.values());
        
        // Vider les anciens résultats de cette paire
        pair.results.removeAll();
        
        // Ajouter les nouveaux résultats UNIQUEMENT à cette paire
        for (BusinessNode node : nodes) {
            pair.results.add(node, user);
        }
        
        ObjectNode response = new ObjectNode(null);
        response.set("resultsCount", new com.fasterxml.jackson.databind.node.IntNode(nodes.size()));
        response.set("pairId", new com.fasterxml.jackson.databind.node.IntNode((int) pairId));
        
        return new EndpointJsonResponse(response, 
            "Filtre appliqué - " + nodes.size() + " résultat(s) capturé(s)");
    }
    
    /**
     * Trouve le FilterResultPair précédent dans la chaîne de filtres progressifs
     */
    private FilterResultPair findPreviousPair(BNode currentNode, FilterResultPair currentPair) {
        if (!(currentNode instanceof SearchForm sf)) {
            return null;
        }
        
        List<FilterResultPair> allPairs = sf.filterChainResults.getElements();
        int currentIndex = -1;
        
        // Trouver l'index du filtre actuel
        for (int i = 0; i < allPairs.size(); i++) {
            if (allPairs.get(i).id() == currentPair.id()) {
                currentIndex = i;
                break;
            }
        }
        
        // Si c'est le premier filtre (index 0) ou non trouvé, retourner null
        if (currentIndex <= 0) {
            return null;
        }
        
        // Retourner le filtre précédent
        FilterResultPair previous = allPairs.get(currentIndex - 1);
        return previous;
    }
    
    private boolean basicSearch(BusinessNode node, String query) {
        if (query == null || query.isEmpty()) return true;
        
        String lowerQuery = query.toLowerCase();
        String nodeName = node.prettyName().toLowerCase();
        
        return nodeName.contains(lowerQuery);
    }
}
