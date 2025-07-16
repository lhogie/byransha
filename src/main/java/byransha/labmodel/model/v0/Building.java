package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.BNode;
import byransha.ListNode;
import byransha.annotations.ListOptions;
import byransha.annotations.ListOptions;
import byransha.annotations.ListOptions;

public class Building extends BusinessNode {

    @byransha.annotations.ListOptions(
        type = byransha.annotations.ListOptions.ListType.LIST
    )
    public ListNode<Office> offices;

    public Building(BBGraph g) {
        super(g);
        offices = BNode.create(g, ListNode.class);
    }

    public Building(BBGraph g, int id) {
        super(g, id);
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
        return "building";
    }

    @Override
    public String whatIsThis() {
        return "Building description";
    }
}
