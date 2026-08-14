package lab;

import byransha.Element;
import byransha.InstantiationParameter;
import byransha.action.base.ShowInKishanView;
import byransha.list.action.ListNode;
import byransha.primitive.StringNode;

public class Contract extends LabElement {
	@ShowInKishanView
	public final StringNode name = fieldNode("name", id -> new StringNode(this, id, null, null));
	@ShowInKishanView
	public Person holder;
	@ShowInKishanView
	public final ListNode<Person> subHolders = fieldNode("subHolders",
			id -> new ListNode<>(this, id, "subHolder(s)", Person.class));
	@ShowInKishanView
	public final ListNode<Person> coordinators = fieldNode("coordinators",
			id -> new ListNode<>(this, id, "coordinators", Person.class));
	@ShowInKishanView
	public final ListNode<Person> partners = fieldNode("partners",
			id -> new ListNode<>(this, id, "partners", Person.class));
	@ShowInKishanView
	public final ListNode<Person> misc = fieldNode("partners", id -> new ListNode<>(this, id, "misc", Person.class));

	public Contract(InstantiationParameter<Element> p) {
		super(p);
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
