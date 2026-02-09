package byransha.filter;

import byransha.BBGraph;
import byransha.BNode;
import byransha.BNode.InstantiationInfo;
import byransha.ListNode;
import byransha.User;
import byransha.annotations.ListOptions;

/**
 * Représente une paire filterChain/results pour permettre à chaque filtre progressif
 * d'avoir sa propre chaîne de filtres et ses propres résultats isolés.
 */
public class FilterResultPair extends BNode {

    public FilterChain filterChain;
    
    @ListOptions(type = ListOptions.ListType.LIST, allowCreation = false, allowMultiple = true)
    public ListNode<BNode> results;

    public FilterResultPair(BBGraph g, User creator, InstantiationInfo ii) {
        super(g, creator, ii);
        endOfConstructor();
    }

    @Override
    protected void createOuts(User creator) {
        super.createOuts(creator);
        filterChain = new FilterChain(g, creator, InstantiationInfo.persisting);
        results = new ListNode<>(g, creator, InstantiationInfo.persisting);
    }

    @Override
    public String whatIsThis() {
        return "Une paire filterChain/results pour un filtre progressif";
    }

    @Override
    public String prettyName() {
        long filledFiltersCount = filterChain.filters.getElements().stream()
            .filter(filter -> filter != null && filter.hasFilledValues())
            .count();
        
        return filledFiltersCount + " filter(s) active - " + results.size() + " result(s)";
    }
}
