package byransha.labmodel.model.v0;

import java.util.HashMap;
import java.util.Map;

import byransha.BBGraph;
import byransha.BNode;
import byransha.ListNode;

public class Lab extends Structure {
	public Person HFDS; // haut fonctionnaire defense securité
	Map<Person, HFDSAvisE> HFDSAvisfds;
	ListNode<Structure> tutelles;

	public Lab(BBGraph g) {
		super(g);
		HFDSAvisfds = new HashMap<>();
		tutelles = BNode.create(g, ListNode.class); // new ListNode<>(graph);
	}

	public Lab(BBGraph g, int id) {
		super(g, id);
	}

	enum HFDSAvisE {
		YES, NO, INBETWEEN
	}

}
