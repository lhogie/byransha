package byransha.lab;

import byransha.Element;
import byransha.ID;
import byransha.Out;
import byransha.list.action.ListNode;

public class Lab extends Structure {

	final ListNode<Structure> tutelles = fieldNode("tutelles", id -> new ListNode(this, id, "tutelles", Structure.class));
	public Out<Person> HFDS = out("tutelles", null); // haut fonctionnaire defense securité

	public Lab(Element g, ID id) {
		super(g, id);
	}

	enum HFDSAvisE {
		YES, NO, INBETWEEN,
	}
}
