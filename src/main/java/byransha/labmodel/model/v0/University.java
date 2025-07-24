package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.BNode;
import byransha.ListNode;

public class University extends Structure {

    ListNode<Campus> campuses;

    public University(BBGraph g) {
        super(g);
        campuses = BNode.create(g, ListNode.class); // new ListNode<>(g);

        status.add(BNode.create(g, IGR.class));
        status.add(BNode.create(g, MCF.class));
        status.add(BNode.create(g, PR.class));
    }

    public University(BBGraph g, int id) {
        super(g, id);
    }
}
