package byransha.nodes.lab;

import byransha.graph.BGraph;
import byransha.graph.BusinessNode;
import byransha.graph.ShowInKishanView;
import byransha.graph.list.action.ListNode;
import byransha.nodes.primitive.StringNode;

public class Contract extends BusinessNode {
	@ShowInKishanView
	public final StringNode name = new StringNode(this);
	@ShowInKishanView
	public Person holder;
	@ShowInKishanView
	public final ListNode<Person> subHolders = new ListNode<>(this, "subHolder(s)", Person.class);
	@ShowInKishanView
	public final ListNode<Person> coordinators = new ListNode<>(this, "coordinators", Person.class);
	@ShowInKishanView
	public final ListNode<Person> partners = new ListNode<>(this, "partners", Person.class);
	@ShowInKishanView
	public final ListNode<Person> misc = new ListNode<>(this, "misc", Person.class);

	public Contract(BGraph g) {
		super(g);
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
