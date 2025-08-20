package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.ListNode;
import byransha.User;

public class University extends Structure {

    ListNode<Campus> campuses;

    public University(BBGraph g, User creator, InstantiationInfo ii) {
        super(g, creator, ii);
        endOfConstructor();
    }

    @Override
    protected void createOuts(User creator) {
        super.createOuts(creator);
        campuses = new ListNode(g, creator, InstantiationInfo.persisting); // new ListNode<>(g);
    }
}
