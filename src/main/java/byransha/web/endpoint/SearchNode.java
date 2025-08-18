package byransha.web.endpoint;

import byransha.*;
import byransha.filter.*;
import byransha.labmodel.model.v0.BusinessNode;
import byransha.web.EndpointJsonResponse;
import byransha.web.NodeEndpoint;
import byransha.web.WebServer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.*;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.sun.net.httpserver.HttpsExchange;
import java.util.*;
import java.util.stream.Collectors;
import toools.text.TextUtilities;

public class SearchNode<N extends BNode> extends NodeEndpoint<BNode> {


    public SearchNode(BBGraph g) {
        super(g);
        endOfConstructor();
    }

    @Override
    public String whatItDoes() {
        return "Searches for existing nodes in the graph by name or type, with pagination support.";
    }

    @Override
    public EndpointJsonResponse exec(
        ObjectNode in,
        User user,
        WebServer webServer,
        HttpsExchange exchange,
        BNode currentNode
    ) throws Throwable {
        ArrayNode dataArray = JsonNodeFactory.instance.arrayNode();
        FilterChain activeFilterChain = null;

        // Get query and filter chain
        final String query = getQueryFromInput(currentNode, in);
        activeFilterChain = getActiveFilterChain(currentNode, in, user);

        // Clear results if using SearchForm
        if (currentNode instanceof SearchForm) {
            ((SearchForm) currentNode).results.removeAll();
        }

        // Pagination parameters
        int page = in.has("page") ? in.get("page").asInt() : 1;
        int pageSize = in.has("pageSize") ? in.get("pageSize").asInt() : 50;

        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 50;

        in.remove("query");
        in.remove("page");
        in.remove("pageSize");
        in.remove("filters");

        // Allow empty queries - users can search with filters only (handled in getQueryFromInput)

        // Search matching nodes with basic query filtering
        var nodes = graph.findAll(BusinessNode.class, node -> {
            if (node.getClass().getSimpleName().equals("SearchForm")) {
                return false;
            }

            // Apply basic query filter if query is provided and not empty
            if (!query.isEmpty() && node.prettyName() != null) {
                return node
                    .prettyName()
                    .toLowerCase()
                    .contains(query.toLowerCase());
            }

            // If no query, include all nodes (filters will handle additional filtering)
            return true;
        });

        // Apply filter chain if present
        if (activeFilterChain != null) {
            nodes = nodes
                .stream()
                .filter(activeFilterChain.toPredicate())
                .collect(Collectors.toList());
        }

        // Sort by Levenshtein distance
        nodes.sort(
            Comparator.comparingInt(node -> {
                String name = node.prettyName();
                if (name == null) return Integer.MAX_VALUE;
                if (query.isEmpty()) return 0; // No distance for empty query
                return TextUtilities.computeLevenshteinDistance(
                    name.toLowerCase(),
                    query
                );
            })
        );

        int total = nodes.size();
        int fromIndex = Math.min((page - 1) * pageSize, total);
        int toIndex = Math.min(fromIndex + pageSize, total);
        List<BusinessNode> pageNodes = nodes.subList(fromIndex, toIndex);

        pageNodes.forEach(node -> {
            if (currentNode instanceof SearchForm sf) {
                sf.results.add(node, user);
            }
            addNodeInfo(dataArray, node);
        });

        // Wrap in metadata
        ObjectNode response = JsonNodeFactory.instance.objectNode();
        response.set("data", dataArray);
        response.put("page", page);
        response.put("pageSize", pageSize);
        response.put("total", total);
        response.put("hasNext", toIndex < total);

        String message;
        if (query.isEmpty() && activeFilterChain == null) {
            message = "All results";
        } else if (query.isEmpty()) {
            message = "Filtered results";
        } else if (activeFilterChain == null) {
            message = "Search results for: " + query;
        } else {
            message =
                "Search results for: " + query + " (with filters applied)";
        }

        return new EndpointJsonResponse(response, message);
    }

    /**
     * Extracts the query string from either SearchForm or direct input.
     */
    private String getQueryFromInput(BNode currentNode, ObjectNode in) {
        if (currentNode instanceof SearchForm && in.isEmpty()) {
            SearchForm searchForm = (SearchForm) currentNode;

            // Get query from searchTerm field for fast search
            String searchText = searchForm.searchTerm.get();
            return searchText != null ? searchText.trim() : "";
        } else {
            // Make query parameter optional for API calls
            if (in.has("query")) {
                String apiQuery = in.get("query").asText();
                return apiQuery != null ? apiQuery.trim() : "";
            }
            return "";
        }
    }

    /**
     * Gets the active FilterChain from either SearchForm or creates one from request.
     */
    private FilterChain getActiveFilterChain(BNode currentNode, ObjectNode in, User creator)
        throws Throwable {
        if (currentNode instanceof SearchForm && in.isEmpty()) {
            SearchForm searchForm = (SearchForm) currentNode;

            // Use the SearchForm's FilterChain if it's enabled
            if (
                searchForm.filterChain != null &&
                searchForm.filterChain.enabled.get()
            ) {
                return searchForm.filterChain;
            }
        } else {
            // Parse custom filters from request and create a FilterChain
            if (in.has("filters") && in.get("filters").isArray()) {
                ArrayNode filtersArray = (ArrayNode) in.get("filters");
                List<FilterNode> customFilters = parseFiltersFromRequest(
                    filtersArray,
                    creator
                );
                if (!customFilters.isEmpty()) {
                    FilterChain filterChain = new FilterChain(graph, creator);
                    filterChain.enabled.set(true, creator);
                    for (FilterNode filter : customFilters) {
                        filterChain.addFilter(filter, creator);
                    }
                    return filterChain;
                }
            }
        }
        return null;
    }

    /**
     * Parses filter configurations from the request JSON.
     *
     * @param filtersArray JSON array containing filter configurations
     * @return List of configured FilterNode instances
     */
    private List<FilterNode> parseFiltersFromRequest(ArrayNode filtersArray, User creator) {
        List<FilterNode> filters = new ArrayList<>();

        for (JsonNode filterNode : filtersArray) {
            if (filterNode.isObject()) {
                ObjectNode filterConfig = (ObjectNode) filterNode;
                FilterNode filter = createFilterFromConfig(filterConfig, creator);
                if (filter != null) {
                    filters.add(filter);
                }
            }
        }

        return filters;
    }

    /**
     * Creates a FilterNode instance from JSON configuration.
     *
     * @param config The JSON configuration for the filter
     * @return FilterNode instance or null if creation fails
     */
    private FilterNode createFilterFromConfig(ObjectNode config, User creator) {
        if (!config.has("type")) {
            System.err.println("Filter configuration missing 'type' field");
            return null;
        }

        String filterType = config.get("type").asText();
        FilterNode filter = null;

        try {
            switch (filterType.toLowerCase()) {
                case "startswith":
                    filter = new StartsWithFilter(graph, creator);
                    break;
                case "contains":
                    filter = new ContainsFilter(graph, creator);
                    break;
                case "class":
                    filter = new ClassFilter(graph, creator);
                    break;
                case "daterange":
                    filter = new DateRangeFilter(graph, creator);
                    break;
                case "numericrange":
                    filter = new NumericRangeFilter(graph, creator);
                    break;
                case "filterchain":
                    filter = new FilterChain(graph, creator);
                    break;
                default:
                    System.err.println("Unknown filter type: " + filterType);
                    return null;
            }

            // Configure the created filter
            if (filter != null) {
                filter.configure(config, creator);
            }

            return filter;
        } catch (Exception e) {
            System.err.println(
                "Error creating filter of type " +
                filterType +
                ": " +
                e.getMessage()
            );
            return null;
        }
    }

    private void addNodeInfo(ArrayNode a, BNode node) {
        ObjectNode nodeInfo = JsonNodeFactory.instance.objectNode();
        nodeInfo.put("id", node.id());
        nodeInfo.put("name", node.prettyName());
        nodeInfo.put("type", node.getClass().getSimpleName());
        nodeInfo.put("isValid", node.isValid());

        node.forEachOutField((name, outNode) -> {
            if (outNode instanceof DocumentNode imageNode) {
                nodeInfo.put("img", imageNode.getAsString());
                nodeInfo.put("imgMimeType", imageNode.mimeType.get());
            }
        });

        a.add(nodeInfo);
    }
}
