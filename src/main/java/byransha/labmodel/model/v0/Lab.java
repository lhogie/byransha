package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.BNode;
import byransha.ListNode;
import java.util.HashMap;
import java.util.Map;

public class Lab extends Structure {

    public Person HFDS; // haut fonctionnaire defense securité
    Map<Person, HFDSAvisE> HFDSAvisfds;
    ListNode<Structure> tutelles;

    public Lab(BBGraph g) {
        super(g);
        HFDSAvisfds = new HashMap<>();
        tutelles = BNode.create(g, ListNode.class);
    }

    public Lab(BBGraph g, int id) {
        super(g, id);
    }

    enum HFDSAvisE {
        YES,
        NO,
        INBETWEEN,
    }
}
