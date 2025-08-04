package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.BNode;
import byransha.ListNode;

public class ResearchGroup extends Structure {

    public ResearchGroup(BBGraph g) {
        super(g);
        keywords = g.create( ListNode.class); // new ListNode<>(g);
    }

    public ResearchGroup(BBGraph g, int id) {
        super(g, id);
    }

    public ListNode<ACMClassifier> keywords;
}
