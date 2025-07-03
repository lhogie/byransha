package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.BNode;
import byransha.ListNode;
import byransha.StringNode;

public class SearchForm extends BusinessNode {

    public StringNode searchTerm;
    public ListNode<BNode> results;

    public SearchForm(BBGraph g) {
        super(g);

        searchTerm = BNode.create(g, StringNode.class);
        results = BNode.create(g, ListNode.class);
    }

    public SearchForm(BBGraph g, int id) {
        super(g, id);
    }

    @Override
    public String whatIsThis() {
        return "The node to search for a precise node in the graph.";
    }

    @Override
    public String prettyName() {
        return "Search Form";
    }
}
