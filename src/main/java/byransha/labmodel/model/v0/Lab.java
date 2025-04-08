package byransha.labmodel.model.v0;

import java.util.HashMap;
import java.util.Map;

import byransha.BBGraph;
import byransha.ListNode;

public class Lab extends Structure {
	public Person HFDS; // haut fonctionnaire defense securité


	public Lab(BBGraph g) {
		super(g);
	}

	enum HFDSAvisE {
		YES, NO, INBETWEEN
	}

	Map<Person, HFDSAvisE> HFDSAvisfds = new HashMap<>();
	ListNode<Structure> tutelles = new ListNode<>(graph);

}
