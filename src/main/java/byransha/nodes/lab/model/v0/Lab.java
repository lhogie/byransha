package byransha.nodes.lab.model.v0;

import byransha.graph.BBGraph;
import byransha.nodes.primitive.ListNode;
import byransha.nodes.system.User;

import java.util.HashMap;
import java.util.Map;

public class Lab extends Structure {

    public Person HFDS; // haut fonctionnaire defense securité
    Map<Person, HFDSAvisE> HFDSAvisfds;
    ListNode<Structure> tutelles;

    public Lab(BBGraph g) {
        super(g);
        HFDSAvisfds = new HashMap<>();
        tutelles = new ListNode(g);
    }

    enum HFDSAvisE {
        YES,
        NO,
        INBETWEEN,
    }
}
