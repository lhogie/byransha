package byransha.nodes.lab;

import byransha.graph.BGraph;
import byransha.nodes.primitive.ListNode;
import byransha.nodes.primitive.StringNode;
import byransha.nodes.system.User;

public class ResearchGroup extends Structure {
    public ListNode<ACMClassifier> keywords;


    public ResearchGroup(BGraph g) {
        super(g);
        keywords = new ListNode<>(g, "research group(s)");
    }

}
