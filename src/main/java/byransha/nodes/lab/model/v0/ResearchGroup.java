package byransha.nodes.lab.model.v0;

import byransha.graph.BBGraph;
import byransha.nodes.primitive.ListNode;
import byransha.nodes.primitive.StringNode;
import byransha.nodes.system.User;

public class ResearchGroup extends Structure {
    public ListNode<ACMClassifier> keywords;


    public ResearchGroup(BBGraph g, User creator) {
        super(g, creator);
        keywords = new ListNode<>(g, creator);
    }

}
