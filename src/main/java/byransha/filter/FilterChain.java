package byransha.filter;

import byransha.*;
import byransha.annotations.ListOptions;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class FilterChain extends FilterNode {

    @ListOptions(type = ListOptions.ListType.LIST, allowCreation = true)
    public ListNode<FilterNode> filters;

    @ListOptions(
        type = ListOptions.ListType.RADIO,
        elementType = ListOptions.ElementType.STRING,
        allowCreation = false,
        source = ListOptions.OptionsSource.PROGRAMMATIC
    )
    public ListNode<StringNode> logicalOperator;

    public FilterChain(BBGraph g) {
        super(g);
        filters = BNode.create(g, ListNode.class);
        logicalOperator = BNode.create(g, ListNode.class);
    }

    public FilterChain(BBGraph g, int id) {
        super(g, id);
    }

    @Override
    protected void initialized() {
        super.initialized();

        var operators = List.of("AND", "OR");
        logicalOperator.setStaticOptions(operators);

        if (logicalOperator.getSelected() == null) {
            StringNode andOption = BNode.create(graph, StringNode.class);
            andOption.set("AND");
            logicalOperator.add(andOption);
        }
    }

    @Override
    public boolean filter(BNode node) {
        List<FilterNode> activeFilters = getActiveFilters();

        if (activeFilters.isEmpty()) {
            return true;
        }

        String operator = logicalOperator.getSelected();
        if (operator == null) {
            operator = "AND";
        }

        switch (operator.toUpperCase()) {
            case "OR":
                return activeFilters
                    .stream()
                    .anyMatch(filter -> applyFilter(filter, node));
            case "AND":
            default:
                return activeFilters
                    .stream()
                    .allMatch(filter -> applyFilter(filter, node));
        }
    }

    private boolean applyFilter(FilterNode filter, BNode node) {
        try {
            if (!filter.supportsNodeType(node.getClass())) {
                return true;
            }

            return filter.filter(node);
        } catch (Exception e) {
            System.err.println(
                "Error applying filter " +
                filter.getClass().getSimpleName() +
                " in FilterChain: " +
                e.getMessage()
            );
            return true;
        }
    }

    private List<FilterNode> getActiveFilters() {
        List<FilterNode> activeFilters = new ArrayList<>();

        for (FilterNode filter : filters.getElements()) {
            if (
                filter != null && filter.enabled != null && filter.enabled.get()
            ) {
                activeFilters.add(filter);
            }
        }

        return activeFilters;
    }

    @Override
    public List<Class<? extends BNode>> getSupportedTypes() {
        return List.of();
    }

    @Override
    public void configure(ObjectNode config) {
        super.configure(config);

        if (config.has("logicalOperator")) {
            logicalOperator.removeAll();
            StringNode operatorNode = BNode.create(graph, StringNode.class);
            operatorNode.set(config.get("logicalOperator").asText());
            logicalOperator.add(operatorNode);
        }

        if (config.has("filters") && config.get("filters").isArray()) {
            ArrayNode filtersArray = (ArrayNode) config.get("filters");

            filters.removeAll();

            for (JsonNode filterConfig : filtersArray) {
                if (filterConfig.isObject()) {
                    ObjectNode filterObj = (ObjectNode) filterConfig;
                    FilterNode filter = createFilterFromConfig(filterObj);
                    if (filter != null) {
                        filters.add(filter);
                    }
                }
            }
        }
    }

    private FilterNode createFilterFromConfig(ObjectNode config) {
        if (!config.has("type")) {
            System.err.println("Filter configuration missing 'type' field");
            return null;
        }

        String filterType = config.get("type").asText();
        FilterNode filter = null;

        try {
            switch (filterType.toLowerCase()) {
                case "startswith":
                    filter = BNode.create(graph, StartsWithFilter.class);
                    break;
                case "contains":
                    filter = BNode.create(graph, ContainsFilter.class);
                    break;
                case "class":
                    filter = BNode.create(graph, ClassFilter.class);
                    break;
                case "daterange":
                    filter = BNode.create(graph, DateRangeFilter.class);
                    break;
                case "numericrange":
                    filter = BNode.create(graph, NumericRangeFilter.class);
                    break;
                case "filterchain":
                    filter = BNode.create(graph, FilterChain.class);
                    break;
                default:
                    System.err.println("Unknown filter type: " + filterType);
                    return null;
            }

            if (filter != null) {
                filter.configure(config);
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

    public void addFilter(FilterNode filter) {
        if (filter != null) {
            filters.add(filter);
        }
    }

    public void removeFilter(FilterNode filter) {
        if (filter != null) {
            filters.remove(filter);
        }
    }

    @Override
    public Predicate<BNode> toPredicate() {
        return node -> {
            if (!enabled.get()) {
                return true;
            }

            try {
                return filter(node);
            } catch (Exception e) {
                System.err.println(
                    "Error applying FilterChain to node " +
                    node.id() +
                    ": " +
                    e.getMessage()
                );
                return true;
            }
        };
    }

    @Override
    public String getFilterDescription() {
        List<FilterNode> activeFilters = getActiveFilters();

        if (activeFilters.isEmpty()) {
            return "Empty filter chain";
        }

        String operator = logicalOperator.getSelected();
        if (operator == null) {
            operator = "AND";
        }

        if (activeFilters.size() == 1) {
            return activeFilters.get(0).getFilterDescription();
        }

        return String.format(
            "Chain of %d filters combined with %s",
            activeFilters.size(),
            operator
        );
    }

    @Override
    public String prettyName() {
        List<FilterNode> activeFilters = getActiveFilters();

        if (activeFilters.isEmpty()) {
            return "Empty Filter Chain";
        }

        String operator = logicalOperator.getSelected();
        if (operator == null) {
            operator = "AND";
        }

        return String.format(
            "Filter Chain (%d filters, %s)",
            activeFilters.size(),
            operator
        );
    }
}
