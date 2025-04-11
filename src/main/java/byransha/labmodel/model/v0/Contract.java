package byransha.labmodel.model.v0;

import java.util.List;

import byransha.BNode;
import byransha.BBGraph;
import byransha.ListNode;
import byransha.StringNode;

public class Contract extends BNode {
	StringNode name;
	Person holder;
	List<Person> subHolders;
	ListNode<Person> coordinators;
	ListNode<Person> partners;
	ListNode<Person> misc;

	public Contract(BBGraph g) {
		super(g);
	}
	@Override
	protected String prettyName() {
		return name.get() + "("+ holder.prettyName() + ")";
	}
	@Override
	public String whatIsThis() {
		return "a contract";
	}


}
