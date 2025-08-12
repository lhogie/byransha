package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.BNode;
import byransha.ListNode;
import byransha.User;

public class University extends Structure {

    ListNode<Campus> campuses;

    public University(BBGraph g, User creator) {
        super(g, creator);
        campuses = new ListNode(g, creator); // new ListNode<>(g);

        status.add(new IGR(g, creator), creator);
        status.add(new MCF(g, creator), creator);
        status.add(new PR(g, creator), creator);
    }

    public University(BBGraph g, int id, User creator) {
        super(g, creator, id);
    }
}
