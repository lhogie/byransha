package byransha.nodes.lab;

import byransha.graph.BGraph;
import byransha.graph.action.list.ListNode;
import byransha.nodes.primitive.StringNode;

public class Contract extends BusinessNode {
	private StringNode name;
	private Person holder;
	ListNode<Person> subHolders;
	ListNode<Person> coordinators;
	ListNode<Person> partners;
	ListNode<Person> misc;

	public Contract(BGraph g) {
		super(g);
		name = new StringNode(g);
		subHolders = new ListNode<>(g, "contracts");
		coordinators = new ListNode<>(g, "coordinators");
		partners = new ListNode<>(g, "partners");
		misc = new ListNode<>(g, "misc");
	}

	@Override
	public String prettyName() {
		return name.get() + "(held by " + holder.prettyName() + ")";
	}

	@Override
	public String whatIsThis() {
		return "a contract";
	}
}
