package byransha.labmodel.model.v0;

import byransha.*;

public class Office extends BusinessNode {

    public StringNode name;
    public ListNode<Person> users;
    public IntNode surface;
    public IntNode capacity;

    public Office(BBGraph g, User creator) {
        super(g, creator);
        name = new StringNode(g, creator);
        users = new ListNode(g, creator);
    }

    public Office(BBGraph g, User creator, int id) {
        super(g, creator, id);
    }

    @Override
    public String whatIsThis() {
        return "an office";
    }

    @Override
    public String prettyName() {
        if (name.get() == null || name.get().isEmpty()) {
            return "Unnamed Office";
        }

        return "Office: " + (name != null ? name.get() : "Unnamed");
    }

    public double occupationRatio() {
        return ((double) capacity.get()) / users.size();
    }

    public double surfacePerUser() {
        return ((double) surface.get()) / users.size();
    }
}
