package byransha.lab;

import byransha.Element;
import byransha.ID;
import byransha.action.base.ShowInKishanView;
import byransha.list.action.ListNode;

public class University extends Structure {
	@ShowInKishanView
	public final ListNode<Campus> campuses = fieldNode("name",  id ->new ListNode<>(this, id, "campus", Campus.class));
	public Person president;

	public University(Element g, ID id) {
		super(g, id);
	}

	@Override
	public String whatIsThis() {
		return "an university";
	}

}
