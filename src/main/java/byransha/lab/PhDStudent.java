package byransha.lab;

import byransha.Element;
import byransha.ID;
import byransha.Out;
import byransha.list.action.ListNode;

public class PhDStudent extends Position {
	final ListNode<Person> directors = fieldNode("directors", id -> new ListNode<Person>(this, id, "directors", Person.class));
	Out<Structure> team = out("team", id -> null);

	public PhDStudent(Element g, ID id) {
		super(g, id);
	}
}
