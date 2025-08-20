package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.ListNode;
import byransha.User;

public class Building extends BusinessNode {

    @byransha.annotations.ListOptions(
        type = byransha.annotations.ListOptions.ListType.LIST
    )
    public ListNode<Office> offices;

    public Building(BBGraph g, User creator, InstantiationInfo ii) {
        super(g, creator, ii);
        endOfConstructor();
    }

    @Override
    protected void createOuts(User creator) {
        offices = new ListNode(g, creator, InstantiationInfo.persisting);
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
