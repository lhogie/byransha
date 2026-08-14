package lab;

import byransha.Element;
import byransha.ID;
import byransha.InstantiationParameter;
import byransha.action.base.ShowInKishanView;
import byransha.list.action.ListNode;

public class University extends Structure {
	@ShowInKishanView
	public final ListNode<Campus> campuses = fieldNode("name", id -> new ListNode<>(this, id, "campus", Campus.class));
	public Person president;

	public University(InstantiationParameter p) {
		super(p);
	}

	public University(Element parent, ID id) {
		super(parent, id);
	}

	@Override
	public String whatIsThis() {
		return "an university";
	}

}
