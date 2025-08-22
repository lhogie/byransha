package byransha.labmodel.model.v0;

import byransha.*;

public class Campus extends BusinessNode {

    public StringNode name;
    public ListNode<Building> buildings;

    public Campus(BBGraph g, User creator, InstantiationInfo ii) {
        super(g, creator, ii);
        endOfConstructor();
    }

    @Override
    protected void createOuts(User creator) {
        super.createOuts(creator);
        name = new  StringNode(g, creator, InstantiationInfo.persisting);
        buildings = new  ListNode(g, creator, InstantiationInfo.persisting);
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
