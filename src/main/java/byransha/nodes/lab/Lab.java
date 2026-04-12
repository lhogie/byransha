package byransha.nodes.lab;

import java.util.HashMap;
import java.util.Map;

import byransha.graph.BGraph;
import byransha.graph.list.action.ListNode;

public class Lab extends Structure {

	final ListNode<Structure> tutelles;
	public Person HFDS; // haut fonctionnaire defense securité
	final Map<Person, HFDSAvisE> HFDSAvisfds;

	public Lab(BGraph g) {
		super(g);
		HFDSAvisfds = new HashMap<>();
		tutelles = new ListNode(g, "tutelles");
	}

	enum HFDSAvisE {
		YES, NO, INBETWEEN,
	}
}
