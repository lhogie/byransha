package byransha.nodes.lab.model.v0;

import byransha.graph.BBGraph;
import byransha.nodes.primitive.ListNode;
import byransha.nodes.primitive.StringNode;
import byransha.nodes.system.User;

public class Building extends BusinessNode {

    public ListNode<Office> offices;
    public StringNode name;

    public Building(BBGraph g) {
        super(g);
        offices = new ListNode(g);
        name = new StringNode(g, "", ".+");
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
