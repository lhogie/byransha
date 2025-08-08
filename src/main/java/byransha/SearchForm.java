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
        searchTerm = g.create(StringNode.class);
        results = g.create(ListNode.class);
        filterChain = g.create(FilterChain.class);
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
        StringNode andOperator = graph.create(StringNode.class);
        andOperator.set("AND");
        filterChain.logicalOperator.add(andOperator);

        // Add a class filter (replaces searchClass)
        ClassFilter classFilter = graph.create(ClassFilter.class);
        classFilter.enabled.set("enabled", classFilter, false); // Start disabled
        classFilter.includeSubclasses.set(
            "includeSubclasses",
            classFilter,
            true
        );
        filterChain.addFilter(classFilter);

        // Add a contains filter for additional text matching
        ContainsFilter containsFilter = graph.create(ContainsFilter.class);
        containsFilter.enabled.set("enabled", containsFilter, false); // Start disabled
        filterChain.addFilter(containsFilter);

        // Add a starts with filter
        StartsWithFilter startsWithFilter = graph.create(
            StartsWithFilter.class
        );
        startsWithFilter.enabled.set("enabled", startsWithFilter, false); // Start disabled
        filterChain.addFilter(startsWithFilter);

        // Add a date range filter
        DateRangeFilter dateFilter = graph.create(DateRangeFilter.class);
        dateFilter.enabled.set("enabled", dateFilter, false); // Start disabled
        filterChain.addFilter(dateFilter);

        // Add a numeric range filter
        NumericRangeFilter numericFilter = graph.create(
            NumericRangeFilter.class
        );
        numericFilter.enabled.set("enabled", numericFilter, false); // Start disabled
        filterChain.addFilter(numericFilter);
    }

    @Override
    public String whatIsThis() {
        return "The node to search for a precise node in the graph with advanced filtering capabilities.";
    }

    @Override
    public String prettyName() {
        return (
            filterChain.filters.getElements().size() +
            " filter(s) - " +
            results.size() +
            " result(s)"
        );
    }
}
