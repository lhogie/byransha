package byransha;

import byransha.annotations.ListOptions;
import byransha.filter.*;
import byransha.labmodel.model.v0.Country;

public class SearchForm extends BNode {

    public StringNode searchTerm;
    public FilterChain filterChain;

    @ListOptions(type = ListOptions.ListType.LIST, allowCreation = false, allowMultiple = true)
    public ListNode<BNode> results;

    public SearchForm(BBGraph g, User creator, InstantiationInfo ii) {
        super(g, creator, ii);
        endOfConstructor();
    }

    @Override
    protected void createOuts(User creator) {
        super.createOuts(creator);

        searchTerm = new StringNode(g, creator, InstantiationInfo.persisting);
        filterChain = new FilterChain(g, creator, InstantiationInfo.persisting);
        results = new ListNode(g, creator, InstantiationInfo.persisting);
        initializeDefaultFilterChain(creator);
    }

    private void initializeDefaultFilterChain(User creator) {
        // Set default logical operator to AND
        StringNode andOperator = new StringNode(g, creator, InstantiationInfo.persisting);
        andOperator.set("AND", creator);
        filterChain.logicalOperator.add(andOperator, creator);

        // Add a class filter (replaces searchClass)
        ClassFilter classFilter = new ClassFilter(g, creator, InstantiationInfo.persisting);
        classFilter.enabled.set(false, creator);
        classFilter.includeSubclasses.set(true, creator);
        filterChain.addFilter(classFilter, creator);

        // Add a contains filter for additional text matching
        ContainsFilter containsFilter = new ContainsFilter(g, creator, InstantiationInfo.persisting);
        containsFilter.enabled.set(false, creator); // Start disabled
        filterChain.addFilter(containsFilter, creator);

        // Add a starts with filter
        StartsWithFilter startsWithFilter = new StartsWithFilter(g, creator, InstantiationInfo.persisting);
        startsWithFilter.enabled.set( false, creator);
        filterChain.addFilter(startsWithFilter, creator);

        // Add a date range filter
        DateRangeFilter dateFilter = new DateRangeFilter(g, creator, InstantiationInfo.persisting);
        dateFilter.enabled.set(false, creator); // Start disabled
        filterChain.addFilter(dateFilter, creator);

        // Add a numeric range filter
        NumericRangeFilter numericFilter = new NumericRangeFilter(g, creator, InstantiationInfo.persisting);
        numericFilter.enabled.set(false, creator); // Start disabled
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
