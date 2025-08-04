package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.BNode;
import byransha.ListNode;

public class University extends Structure {

    ListNode<Campus> campuses;

    public University(BBGraph g) {
        super(g);
        campuses = g.create( ListNode.class); // new ListNode<>(g);

        status.add(g.create( IGR.class));
        status.add(g.create( MCF.class));
        status.add(g.create( PR.class));
    }

    public University(BBGraph g, int id) {
        super(g, id);
    }
}
