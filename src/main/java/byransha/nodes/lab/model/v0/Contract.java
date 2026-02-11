package byransha.nodes.lab.model.v0;

import byransha.BBGraph;
import byransha.annotations.Required;
import byransha.nodes.system.User;
import byransha.nodes.primitive.ListNode;
import byransha.nodes.primitive.StringNode;

public class Contract extends BusinessNode {
    @Required
    private StringNode name;

    private Person holder;
    ListNode<Person> subHolders;
    ListNode<Person> coordinators;
    ListNode<Person> partners;
    ListNode<Person> misc;

    public Contract(BBGraph g, User creator) {
        super(g, creator);
        name = new StringNode(g, creator);
        subHolders = new ListNode<>(g, creator);
        coordinators = new ListNode<>(g, creator);
        partners = new ListNode<>(g, creator);
        misc = new ListNode<>(g, creator);
    }

    @Override
    public String prettyName() {
        return name.get() + "(held by " + holder.prettyName() + ")";
    }

    @Override
    public String whatIsThis() {
        return "a contract";
    }
}
