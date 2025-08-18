package byransha;

import byransha.annotations.ListOptions;
import byransha.filter.*;

public class SearchForm extends BNode {

    public StringNode searchTerm;

    @ListOptions(type = ListOptions.ListType.LIST, allowCreation = false)
    public ListNode<BNode> results;

    public FilterChain filterChain;

    public SearchForm(BBGraph g, User creator) {
        super(g, creator);
        searchTerm = new StringNode(g, creator);
        results = new ListNode(g, creator);
        filterChain = new FilterChain(g, creator);
        initializeDefaultFilterChain(creator);
        endOfConstructor();
    }

    public SearchForm(BBGraph g, User creator, int id) {
        super(g, creator, id);
        endOfConstructor();
    }

    private void initializeDefaultFilterChain(User creator) {
        // Set default logical operator to AND
        StringNode andOperator = new StringNode(graph, creator);
        andOperator.set("AND", creator);
        filterChain.logicalOperator.add(andOperator, creator);

        // Add a class filter (replaces searchClass)
        ClassFilter classFilter = new ClassFilter(graph, creator);
        classFilter.enabled.set("enabled", classFilter, false, creator); // Start disabled
        classFilter.includeSubclasses.set(
            "includeSubclasses",
            classFilter,
            true,
                creator
        );
        filterChain.addFilter(classFilter, creator);

        // Add a contains filter for additional text matching
        ContainsFilter containsFilter = new ContainsFilter(graph, creator);
        containsFilter.enabled.set("enabled", containsFilter, false, creator); // Start disabled
        filterChain.addFilter(containsFilter, creator);

        // Add a starts with filter
        StartsWithFilter startsWithFilter = new StartsWithFilter(graph, creator);
        startsWithFilter.enabled.set("enabled", startsWithFilter, false, creator); // Start disabled
        filterChain.addFilter(startsWithFilter, creator);

        // Add a date range filter
        DateRangeFilter dateFilter = new DateRangeFilter(graph, creator);
        dateFilter.enabled.set("enabled", dateFilter, false, creator); // Start disabled
        filterChain.addFilter(dateFilter, creator);

        // Add a numeric range filter
        NumericRangeFilter numericFilter = new NumericRangeFilter(graph, creator);
        numericFilter.enabled.set("enabled", numericFilter, false, creator); // Start disabled
        filterChain.addFilter(numericFilter, creator);
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
