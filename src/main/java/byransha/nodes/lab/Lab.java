package byransha.nodes.lab;

import java.util.HashMap;
import java.util.Map;

import byransha.graph.BNode;
import byransha.graph.list.action.ListNode;

public class Lab extends Structure {

	final ListNode<Structure> tutelles = new ListNode(this, "tutelles", Structure.class);
	public Person HFDS; // haut fonctionnaire defense securité
	final Map<Person, HFDSAvisE> HFDSAvisfds;

	public Lab(BNode g) {
		super(g);
		HFDSAvisfds = new HashMap<>();
	}

	enum HFDSAvisE {
		YES, NO, INBETWEEN,
	}
}
