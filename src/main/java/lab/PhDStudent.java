package lab;

import byransha.Element;
import byransha.ID;
import byransha.InstantiationParameters;
import byransha.Out;
import byransha.list.action.ListNode;

public class PhDStudent extends Position {
	final ListNode<Person> directors = fieldNode("directors", id -> new ListNode<Person>(this, id, "directors", Person.class));
	Out<Structure> team = out("team", id -> null);

	public PhDStudent(InstantiationParameters p) {
		super(p);
	}
}
