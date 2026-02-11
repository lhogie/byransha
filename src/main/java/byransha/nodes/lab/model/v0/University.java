package byransha.nodes.lab.model.v0;

import byransha.BBGraph;
import byransha.nodes.primitive.ListNode;
import byransha.nodes.system.User;

public class University extends Structure {
    ListNode<Campus> campuses;

    public University(BBGraph g, User creator) {
        super(g, creator);
        campuses = new ListNode(g, creator);
    }
}
