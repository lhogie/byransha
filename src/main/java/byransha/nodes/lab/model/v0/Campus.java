package byransha.nodes.lab.model.v0;

import byransha.BBGraph;
import byransha.annotations.Required;
import byransha.nodes.system.User;
import byransha.nodes.primitive.ListNode;
import byransha.nodes.primitive.StringNode;

public class Campus extends BusinessNode {

    @Required
    public StringNode name;

    public ListNode<Building> buildings;

    public Campus(BBGraph g, User creator) {
        super(g, creator);
        name = new  StringNode(g, creator, "");
        buildings = new  ListNode(g, creator);
    }

    @Override
    public String whatIsThis() {
        return "a campus";
    }
}
