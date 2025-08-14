package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.ListNode;
import byransha.User;

public class ResearchGroup extends Structure {

    public ResearchGroup(BBGraph g, User creator) {
        super(g, creator);
        keywords = new ListNode(g, creator); // new ListNode<>(g);
        endOfConstructor();
    }

    public ResearchGroup(BBGraph g, User creator, int id) {
        super(g, creator, id);
        endOfConstructor();
    }

    public ListNode<ACMClassifier> keywords;
}
