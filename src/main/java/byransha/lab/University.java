package byransha.lab;

import byransha.ID;
import byransha.graph.Element;
import byransha.graph.ShowInKishanView;
import byransha.graph.list.action.ListNode;

public class University extends Structure {
	@ShowInKishanView
	public final ListNode<Campus> campuses = lookupOrCreate("name",  id ->new ListNode<>(this, id, "campus", Campus.class));
	public Person president;

	public University(Element g, ID id) {
		super(g, id);
	}

	@Override
	public String whatIsThis() {
		return "an university";
	}

}
