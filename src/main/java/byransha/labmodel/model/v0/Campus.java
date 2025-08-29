package byransha.labmodel.model.v0;

import byransha.*;
import byransha.annotations.Required;

public class Campus extends BusinessNode {

    @Required
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
        if(name != null && name.get() != null && !name.get().isBlank()) {
            return "Campus : " + name.get();
        }
        return null;
    }

    @Override
    public String whatIsThis() {
        return "Campus: " + name.get();
    }
}
