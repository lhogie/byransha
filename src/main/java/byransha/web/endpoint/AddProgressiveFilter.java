package byransha.web.endpoint;

import byransha.*;
import byransha.filter.*;
import byransha.web.EndpointJsonResponse;
import byransha.web.NodeEndpoint;
import byransha.web.WebServer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpsExchange;

/**
 * Endpoint pour créer un nouveau filtre progressif (FilterResultPair) dans un SearchForm
 */
public class AddProgressiveFilter extends NodeEndpoint<BNode> {
    
    public AddProgressiveFilter(BBGraph g) {
        super(g);
        endOfConstructor();
    }

    @Override
    public String whatItDoes() {
        return "Crée un nouveau filtre progressif avec sa propre FilterChain et liste de résultats";
    }

    @Override
    public EndpointJsonResponse exec(
        ObjectNode in,
        User user,
        WebServer webServer,
        HttpsExchange exchange,
        BNode currentNode
    ) throws Throwable {
        
        if (!(currentNode instanceof SearchForm)) {
            return new EndpointJsonResponse(
                new ObjectNode(null),
                "Ce endpoint nécessite un nœud SearchForm"
            );
        }

        SearchForm searchForm = (SearchForm) currentNode;
        
        // Créer une nouvelle paire FilterChain/Results
        FilterResultPair newPair = new FilterResultPair(g, user, BNode.InstantiationInfo.persisting);
        
        // Initialiser les filtres par défaut dans la nouvelle FilterChain
        initializeDefaultFilters(newPair.filterChain, user);
        
        // Ajouter la paire à la liste
        searchForm.filterChainResults.add(newPair, user);
        
        // Publier un événement pour l'audit
        g.getEventBus().publishEvent(
            byransha.event.EventType.FILTER_CREATED,
            newPair,
            user,
            "Progressive filter created in SearchForm #" + searchForm.id()
        );
        
        // Retourner les informations de la nouvelle paire
        ObjectNode response = new ObjectNode(null);
        response.set("filterChainId", new com.fasterxml.jackson.databind.node.IntNode(newPair.filterChain.id()));
        response.set("resultsId", new com.fasterxml.jackson.databind.node.IntNode(newPair.results.id()));
        response.set("pairId", new com.fasterxml.jackson.databind.node.IntNode(newPair.id()));
        
        return new EndpointJsonResponse(response, "Nouveau filtre progressif créé");
    }
    
    private void initializeDefaultFilters(FilterChain filterChain, User creator) {
        // Set default logical operator to AND
        StringNode andOperator = new StringNode(g, creator, BNode.InstantiationInfo.persisting);
        andOperator.set("AND", creator);
        filterChain.logicalOperator.add(andOperator, creator);

        // Add a class filter
        ClassFilter classFilter = new ClassFilter(g, creator, BNode.InstantiationInfo.persisting);
        classFilter.includeSubclasses.set(true, creator);
        filterChain.addFilter(classFilter, creator);

        // Add a contains filter
        ContainsFilter containsFilter = new ContainsFilter(g, creator, BNode.InstantiationInfo.persisting);
        filterChain.addFilter(containsFilter, creator);

        // Add a starts with filter
        StartsWithFilter startsWithFilter = new StartsWithFilter(g, creator, BNode.InstantiationInfo.persisting);
        filterChain.addFilter(startsWithFilter, creator);

        // Add a date range filter
        DateRangeFilter dateFilter = new DateRangeFilter(g, creator, BNode.InstantiationInfo.persisting);
        filterChain.addFilter(dateFilter, creator);

        // Add a numeric range filter
        NumericRangeFilter numericFilter = new NumericRangeFilter(g, creator, BNode.InstantiationInfo.persisting);
        filterChain.addFilter(numericFilter, creator);
    }
}
