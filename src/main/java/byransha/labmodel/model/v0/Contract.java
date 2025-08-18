package byransha.labmodel.model.v0;

import byransha.*;

import java.util.List;

public class Contract extends BusinessNode {

    private Out<StringNode> name;
    private Out<Person> holder;
    ListNode<Person> subHolders;
    ListNode<Person> coordinators;
    ListNode<Person> partners;
    ListNode<Person> misc;

    public Contract(BBGraph g, User creator) {
        super(g, creator);
        endOfConstructor();
    }

    public Contract(BBGraph g, User creator, int id) {
        super(g, creator, id);
        endOfConstructor();
    }

    @Override
    public String prettyName() {
        if (name == null || name.get() == null || name.get().get().isEmpty()) {
            System.err.println("Contract with no name: " + this);
            return "Contract(unknown)";
        }
        return name.get() + "(" + holder.prettyName() + ")";
    }

    @Override
    public String whatIsThis() {
        return "a contract";
    }
}
