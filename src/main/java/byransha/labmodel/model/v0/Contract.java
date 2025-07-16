package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.ListNode;
import byransha.StringNode;
import byransha.annotations.ListOptions;
import byransha.annotations.ListOptions;
import byransha.annotations.ListOptions;
import byransha.annotations.ListOptions;
import byransha.annotations.ListOptions;
import java.util.List;

public class Contract extends BusinessNode {

    StringNode name;
    Person holder;
    List<Person> subHolders;
    ListNode<Person> coordinators;
    ListNode<Person> partners;
    ListNode<Person> misc;

    public Contract(BBGraph g) {
        super(g);
    }

    public Contract(BBGraph g, int id) {
        super(g, id);
    }

    @Override
    public String prettyName() {
        if (name == null || name.get() == null || name.get().isEmpty()) {
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
