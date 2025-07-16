package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.BNode;
import byransha.ListNode;
import byransha.annotations.ListOptions;
import byransha.annotations.ListOptions;
import byransha.annotations.ListOptions;

public class ResearchGroup extends Structure {

    public ResearchGroup(BBGraph g) {
        super(g);
        keywords = BNode.create(g, ListNode.class); // new ListNode<>(g);
    }

    public ResearchGroup(BBGraph g, int id) {
        super(g, id);
    }

    public ListNode<ACMClassifier> keywords;
}
