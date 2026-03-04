package byransha.nodes.lab;

import byransha.graph.BGraph;
import byransha.nodes.primitive.ListNode;
import byransha.nodes.system.User;

public class University extends Structure {
    ListNode<Campus> campuses;

    public University(BGraph g) {
        super(g);
        campuses = new ListNode(g, "campus");
    }
}
