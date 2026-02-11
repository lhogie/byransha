package byransha;

import byransha.annotations.ListOptions;
import byransha.filter.ClassFilter;
import byransha.filter.ContainsFilter;
import byransha.filter.DateRangeFilter;
import byransha.filter.FilterChain;
import byransha.filter.FilterNode; // ajout Dylan
import byransha.filter.NumericRangeFilter;
import byransha.filter.StartsWithFilter;
import byransha.nodes.BNode;
import byransha.nodes.system.User;
import byransha.nodes.primitive.ListNode;
import byransha.nodes.primitive.StringNode;

public class SearchForm extends BNode {

    public StringNode searchTerm;
    public FilterChain filterChain;

    @ListOptions(type = ListOptions.ListType.LIST, allowCreation = false, allowMultiple = true)
    public ListNode<BNode> results;

    public SearchForm(BBGraph g, User creator) {
        super(g, creator);
        searchTerm = new StringNode(g, creator);
        filterChain = new FilterChain(g, creator);
        results = new ListNode(g, creator);


        // Set default logical operator to AND
        StringNode andOperator = new StringNode(g, creator);
        andOperator.set("AND", creator);
        filterChain.logicalOperator.add(andOperator, creator);

        // Add a class filter (replaces searchClass)
        ClassFilter classFilter = new ClassFilter(g, creator);
        classFilter.includeSubclasses.set(true, creator);
        filterChain.addFilter(classFilter, creator);

        // Add a contains filter for additional text matching
        ContainsFilter containsFilter = new ContainsFilter(g, creator);
        filterChain.addFilter(containsFilter, creator);

        // Add a starts with filter
        StartsWithFilter startsWithFilter = new StartsWithFilter(g, creator);
        filterChain.addFilter(startsWithFilter, creator);

        // Add a date range filter
        DateRangeFilter dateFilter = new DateRangeFilter(g, creator);
        filterChain.addFilter(dateFilter, creator);

        // Add a numeric range filter
        NumericRangeFilter numericFilter = new NumericRangeFilter(g, creator);
        filterChain.addFilter(numericFilter, creator);
    }

    @Override
    public String whatIsThis() {
        return "The node to search for a precise node in the graph with advanced filtering capabilities.";
    }

    @Override
    public String prettyName() {
        long filledFiltersCount = filterChain.filters.getElements().stream()
            .filter(filter -> {
                if (filter instanceof FilterNode fn) {
                    return fn.hasFilledValues();
                }else{
                    return false;
                }
            })
            .count();

        return filledFiltersCount +" filter(s) - " + results.size() + " result(s)";
    }
}
