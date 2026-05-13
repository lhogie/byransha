package byransha.nodes.lab;

import byransha.graph.BNode;
import byransha.graph.ShowInKishanView;
import byransha.graph.list.action.ListNode;

public class University extends Structure {
	@ShowInKishanView
	public final ListNode<Campus> campuses = new ListNode<>(this, "campus", Campus.class);
	public Person president;

	public University(BNode g) {
		super(g);
	}

	@Override
	public String whatIsThis() {
		return "an university";
	}

}
