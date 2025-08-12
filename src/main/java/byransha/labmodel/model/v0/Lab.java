package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.BNode;
import byransha.ListNode;
import byransha.User;

import java.util.HashMap;
import java.util.Map;

public class Lab extends Structure {

    public Person HFDS; // haut fonctionnaire defense securité
    Map<Person, HFDSAvisE> HFDSAvisfds;
    ListNode<Structure> tutelles;

    public Lab(BBGraph g, User creator) {
        super(g, creator);
        HFDSAvisfds = new HashMap<>();
        tutelles = new ListNode(g, creator);
    }

    public Lab(BBGraph g, User creator, int id) {
        super(g, creator, id);
    }

    enum HFDSAvisE {
        YES,
        NO,
        INBETWEEN,
    }
}
