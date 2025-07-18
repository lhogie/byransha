package byransha;

import byransha.annotations.ListOptions;
import byransha.labmodel.model.v0.BusinessNode;
import java.util.ArrayList;

public class SearchForm extends PersistingNode {

    public StringNode searchTerm;

    @ListOptions(
        type = ListOptions.ListType.RADIO,
        elementType = ListOptions.ElementType.STRING,
        allowCreation = false,
        source = ListOptions.OptionsSource.PROGRAMMATIC
    )
    public ListNode<StringNode> searchClass;

    @ListOptions(type = ListOptions.ListType.LIST, allowCreation = false)
    public ListNode<BNode> results;

    public SearchForm(BBGraph g) {
        super(g);
        searchTerm = BNode.create(g, StringNode.class);
        searchClass = BNode.create(g, ListNode.class);
        results = BNode.create(g, ListNode.class);
    }

    public SearchForm(BBGraph g, int id) {
        super(g, id);
    }

    @Override
    protected void initialized() {
        super.initialized();

        var allClass = new ArrayList<String>();
        graph.forEachNode(node -> {
            if (
                node instanceof BusinessNode &&
                allClass
                    .stream()
                    .noneMatch(s -> s.equals(node.getClass().getSimpleName()))
            ) {
                allClass.add(node.getClass().getSimpleName());
            }
        });

        searchClass.setStaticOptions(allClass);
    }

    @Override
    public String whatIsThis() {
        return "The node to search for a precise node in the graph.";
    }

    @Override
    public String prettyName() {
        if (searchTerm.get() == null) return "Search Form";
        else return searchTerm.get();
    }
}
