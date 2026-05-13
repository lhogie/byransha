package byransha.nodes.lab;

import byransha.graph.BNode;
import byransha.graph.BusinessNode;
import byransha.graph.ShowInKishanView;
import byransha.graph.list.action.ListNode;
import byransha.nodes.primitive.StringNode;

public class Publication extends BusinessNode {
	@ShowInKishanView
	public final StringNode title = new StringNode(this, null, ".+");
	@ShowInKishanView
	public ListNode<Person> authors = new ListNode<>(this, "author(s)", Person.class);
	@ShowInKishanView
	public final StringNode halID = new StringNode(this, null, "^hal-\\d+$");

	public ListNode<ACMClassifier> acmClassifier = new ListNode<>(this, "ACM classifiers", ACMClassifier.class);

	public Publication(BNode g) {
		super(g);
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
