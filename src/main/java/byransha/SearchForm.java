package byransha;

import byransha.annotations.ListSettings;
import byransha.labmodel.model.v0.BusinessNode;

import java.util.ArrayList;

public class SearchForm extends PersistingNode{

    public StringNode searchTerm;
    public RadioNode<String> searchClass;
    public ListNode<BNode> results;

    public SearchForm(BBGraph g) {
        super(g);

        searchTerm = BNode.create(g, StringNode.class);
        searchClass = BNode.create(g, RadioNode.class);
        results = BNode.create(g, ListNode.class);

        var allClass = new ArrayList<String>();
        g.forEachNode(node-> {
            if (node instanceof BusinessNode && allClass.stream().noneMatch(s -> s.equals(node.getClass().getSimpleName()))) {
                allClass.add(node.getClass().getSimpleName());
            }
        });

        allClass.forEach(c -> {searchClass.addOption(c);});

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
        if(searchTerm.get() == null) return "Search Form";
        else return searchTerm.get();
    }
}
