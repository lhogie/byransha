package byransha.labmodel.model.v0;

import byransha.*;
import byransha.annotations.Required;

import java.util.List;

public class Contract extends BusinessNode {
    @Required
    private StringNode name;

    private Out<Person> holder;
    ListNode<Person> subHolders;
    ListNode<Person> coordinators;
    ListNode<Person> partners;
    ListNode<Person> misc;

    public Contract(BBGraph g, User creator, InstantiationInfo ii) {
        super(g, creator, ii);
        endOfConstructor();
    }

    @Override
    protected void createOuts(User creator) {
        super.createOuts(creator);
        holder = new Out<>(g, creator, InstantiationInfo.persisting);
        name = new StringNode(g, creator, InstantiationInfo.persisting);
        subHolders = new ListNode<>(g, creator, InstantiationInfo.persisting);
        coordinators = new ListNode<>(g, creator, InstantiationInfo.persisting);
        partners = new ListNode<>(g, creator, InstantiationInfo.persisting);
        misc = new ListNode<>(g, creator, InstantiationInfo.persisting);
    }

    @Override
    public String prettyName() {
        if (name == null || name.get() == null || name.get().isEmpty()) {
            return null;
        }
        return name.get() + "(" + holder.prettyName() + ")";
    }

    @Override
    public String whatIsThis() {
        return "a contract";
    }
}
