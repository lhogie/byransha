package byransha.nodes.lab.model.v0;

import java.util.HashMap;
import java.util.Map;

import byransha.graph.BBGraph;
import byransha.nodes.primitive.ListNode;

public class Lab extends Structure {

	public Person HFDS; // haut fonctionnaire defense securité
	final Map<Person, HFDSAvisE> HFDSAvisfds;
	final ListNode<Structure> tutelles;

	public Lab(BBGraph g) {
		super(g);
		HFDSAvisfds = new HashMap<>();
		tutelles = new ListNode(g);
	}

	enum HFDSAvisE {
		YES, NO, INBETWEEN,
	}
}
