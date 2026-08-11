package byransha.lab;

import byransha.ID;
import byransha.graph.Element;
import byransha.graph.LabNode;
import byransha.graph.ShowInKishanView;
import byransha.graph.list.action.ListNode;
import byransha.primitive.StringNode;

public class Contract extends LabNode {
	@ShowInKishanView
	public final StringNode name = lookupOrCreate("name", id -> new StringNode(this, id, null, null));
	@ShowInKishanView
	public Person holder;
	@ShowInKishanView
	public final ListNode<Person> subHolders = lookupOrCreate("subHolders",
			id -> new ListNode<>(this, id, "subHolder(s)", Person.class));
	@ShowInKishanView
	public final ListNode<Person> coordinators = lookupOrCreate("coordinators",
			id -> new ListNode<>(this, id, "coordinators", Person.class));
	@ShowInKishanView
	public final ListNode<Person> partners = lookupOrCreate("partners", id -> new ListNode<>(this, id, "partners", Person.class));
	@ShowInKishanView
	public final ListNode<Person> misc = lookupOrCreate("partners", id -> new ListNode<>(this, id, "misc", Person.class));

	public Contract(Element g, ID id) {
		super(g, id);
	}

	@Override
	public String toString() {
		return name.get() + "(held by " + holder + ")";
	}

	@Override
	public String whatIsThis() {
		return "a contract";
	}
}
