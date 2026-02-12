package byransha.nodes.lab.model.v0;

import byransha.graph.BBGraph;
import byransha.nodes.primitive.ListNode;
import byransha.nodes.primitive.StringNode;
import byransha.nodes.system.User;

public class Building extends BusinessNode {

    public ListNode<Office> offices;
    public StringNode name;

    public Building(BBGraph g, User creator) {
        super(g, creator);
        offices = new ListNode(g, creator);
        name = new StringNode(g, creator, "", ".+");
    }

    public Office findOffice(String name) {
        for (var o : offices.getElements()) {
            if (o.name.get().equals(name)) {
                return o;
            }
        }

        return null;
    }

    @Override
    public String prettyName() {
        return name.get();
    }

    @Override
    public String whatIsThis() {
        return "a building in a campus";
    }
}
