package byransha.lab;

import byransha.ID;
import byransha.graph.Element;
import byransha.graph.LabNode;
import byransha.graph.ShowInKishanView;
import byransha.graph.list.action.ListNode;
import byransha.primitive.StringNode;

public class Publication extends LabNode {
	@ShowInKishanView
	public final StringNode title = lookupOrCreate("title", id -> new StringNode(this, id, null, ".+"));
	@ShowInKishanView
	public ListNode<Person> authors = lookupOrCreate("authors", id -> new ListNode<>(this, id, "author(s)", Person.class));
	@ShowInKishanView
	public final StringNode halID = lookupOrCreate("halID", id -> new StringNode(this, id, null, "^hal-\\d+$"));

	public ListNode<ACMClassifier> acmClassifier = lookupOrCreate("acmClassifier", id -> new ListNode<>(this, id, "ACM classifiers", ACMClassifier.class));

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
