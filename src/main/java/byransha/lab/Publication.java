package byransha.lab;

import byransha.Element;
import byransha.ID;
import byransha.action.base.ShowInKishanView;
import byransha.list.action.ListNode;
import byransha.primitive.StringNode;

public class Publication extends LabElement {
	@ShowInKishanView
	public final StringNode title = fieldNode("title", id -> new StringNode(this, id, null, ".+"));
	@ShowInKishanView
	public ListNode<Person> authors = fieldNode("authors", id -> new ListNode<>(this, id, "author(s)", Person.class));
	@ShowInKishanView
	public final StringNode halID = fieldNode("halID", id -> new StringNode(this, id, null, "^hal-\\d+$"));

	public ListNode<ACMClassifier> acmClassifier = fieldNode("acmClassifier", id -> new ListNode<>(this, id, "ACM classifiers", ACMClassifier.class));

	public Publication(Element g, ID id) {
		super(g, id);
	}

	@Override
	public String whatIsThis() {
		return "a publication";
	}

	@Override
	public String toString() {
		return title.toString();
	}
}
