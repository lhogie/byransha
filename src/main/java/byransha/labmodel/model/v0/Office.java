package byransha.labmodel.model.v0;

import byransha.*;
import byransha.annotations.Required;

public class Office extends BusinessNode {

    @Required
    public StringNode name;

    public ListNode<Person> users;

    @Required
    public IntNode surface, capacity;

    public Office(BBGraph g, User creator, InstantiationInfo ii) {
        super(g, creator, ii);
        endOfConstructor();
    }

    @Override
    protected void createOuts(User creator) {
        super.createOuts(creator);
        name = new StringNode(g, creator, InstantiationInfo.persisting);
        users = new ListNode(g, creator, InstantiationInfo.persisting);
        surface = new IntNode(g, creator, InstantiationInfo.persisting);
        capacity = new IntNode(g, creator, InstantiationInfo.persisting);
    }

    @Override
    public String whatIsThis() {
        return "an office";
    }

    @Override
    public String prettyName() {
        if (name != null) {
            return "Office: " + name.get();
        }
        return null;
    }

    public double occupationRatio() {
        return ((double) capacity.get()) / users.size();
    }

    public double surfacePerUser() {
        return ((double) surface.get()) / users.size();
    }
}
