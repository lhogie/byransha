package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.BNode;
import byransha.ListNode;
import byransha.User;

public class ResearchGroup extends Structure {

    public ResearchGroup(BBGraph g, User creator) {
        super(g, creator);
        keywords = new ListNode(g, creator); // new ListNode<>(g);
    }

    public ResearchGroup(BBGraph g, User creator, int id) {
        super(g, creator, id);
    }

    public ListNode<ACMClassifier> keywords;
}
