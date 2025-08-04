package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.BNode;
import byransha.ListNode;
import byransha.StringNode;

public class Campus extends BusinessNode {

    public StringNode name;
    public ListNode<Building> buildings;

    public Campus(BBGraph g) {
        super(g);
        name = g.create( StringNode.class);
        buildings = g.create( ListNode.class);
    }

    public Campus(BBGraph g, int id) {
        super(g, id);
    }

    @Override
    public String prettyName() {
        return "campus";
    }

    @Override
    public String whatIsThis() {
        return "Campus: " + name.get();
    }
}
