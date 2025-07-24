package byransha;

import byransha.annotations.ListOptions;
import byransha.filter.*;

public class SearchForm extends PersistingNode {

    public StringNode searchTerm;

    @ListOptions(type = ListOptions.ListType.LIST, allowCreation = false)
    public ListNode<BNode> results;

    public FilterChain filterChain;

    public SearchForm(BBGraph g) {
        super(g);
        searchTerm = BNode.create(g, StringNode.class);
        results = BNode.create(g, ListNode.class);
        filterChain = BNode.create(g, FilterChain.class);
    }

    public SearchForm(BBGraph g, int id) {
        super(g, id);
    }

    @Override
    protected void initialized() {
        super.initialized();

        // Initialize the filter chain with common filters
        initializeDefaultFilterChain();
    }

    private void initializeDefaultFilterChain() {
        // Set default logical operator to AND
        StringNode andOperator = BNode.create(graph, StringNode.class);
        andOperator.set("AND");
        filterChain.logicalOperator.add(andOperator);

        // Add a class filter (replaces searchClass)
        ClassFilter classFilter = BNode.create(graph, ClassFilter.class);
        classFilter.enabled.set(false); // Start disabled
        classFilter.includeSubclasses.set(true);
        filterChain.addFilter(classFilter);

        // Add a contains filter for additional text matching
        ContainsFilter containsFilter = BNode.create(
            graph,
            ContainsFilter.class
        );
        containsFilter.enabled.set(false); // Start disabled
        filterChain.addFilter(containsFilter);

        // Add a starts with filter
        StartsWithFilter startsWithFilter = BNode.create(
            graph,
            StartsWithFilter.class
        );
        startsWithFilter.enabled.set(false); // Start disabled
        filterChain.addFilter(startsWithFilter);

        // Add a date range filter
        DateRangeFilter dateFilter = BNode.create(graph, DateRangeFilter.class);
        dateFilter.enabled.set(false); // Start disabled
        filterChain.addFilter(dateFilter);

        // Add a numeric range filter
        NumericRangeFilter numericFilter = BNode.create(
            graph,
            NumericRangeFilter.class
        );
        numericFilter.enabled.set(false); // Start disabled
        filterChain.addFilter(numericFilter);
    }

    @Override
    public String whatIsThis() {
        return "The node to search for a precise node in the graph with advanced filtering capabilities.";
    }

    @Override
    public String prettyName() {
        String searchText = searchTerm.get();
        if (searchText == null || searchText.trim().isEmpty()) {
            // Check if any filters are enabled to show appropriate name
            if (filterChain != null && filterChain.enabled.get()) {
                long enabledFilters = filterChain.filters
                    .getElements()
                    .stream()
                    .filter(filter -> filter.enabled.get())
                    .count();
                if (enabledFilters > 0) {
                    return "Search Form (with " + enabledFilters + " filters)";
                }
            }
            return "Search Form";
        } else {
            return "Search: " + searchText;
        }
    }
}
