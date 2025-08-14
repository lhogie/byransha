package byransha.labmodel.model.v0;

import byransha.*;

public class Campus extends BusinessNode {

    public StringNode name;
    public ListNode<Building> buildings;

    public Campus(BBGraph g, User creator) {
        super(g, creator);
        name = new  StringNode(g, creator);
        buildings = new  ListNode(g, creator);
        endOfConstructor();
    }

    public Campus(BBGraph g, int id, User creator) {
        super(g, creator, id);
        endOfConstructor();
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
