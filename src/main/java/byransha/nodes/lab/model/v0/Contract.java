package byransha.nodes.lab.model.v0;

import byransha.graph.BBGraph;
import byransha.nodes.primitive.ListNode;
import byransha.nodes.primitive.StringNode;
import byransha.nodes.system.User;

public class Contract extends BusinessNode {
	private StringNode name;
	private Person holder;
	ListNode<Person> subHolders;
	ListNode<Person> coordinators;
	ListNode<Person> partners;
	ListNode<Person> misc;

	public Contract(BBGraph g) {
		super(g);
		name = new StringNode(g);
		subHolders = new ListNode<>(g);
		coordinators = new ListNode<>(g);
		partners = new ListNode<>(g);
		misc = new ListNode<>(g);
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
