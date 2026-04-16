package byransha.nodes.lab;

import byransha.graph.BGraph;
import byransha.graph.BusinessNode;
import byransha.graph.ShowInKishanView;
import byransha.graph.list.action.ListNode;
import byransha.nodes.primitive.StringNode;

public class Publication extends BusinessNode {
	@ShowInKishanView
	public StringNode title;
	@ShowInKishanView
	public ListNode<Person> authors = new ListNode<>(g, "author(s)", Person.class);
	@ShowInKishanView
	public final StringNode halID = new StringNode(g, null, "^hal-\\d+$");

	public ListNode<ACMClassifier> acmClassifier = new ListNode<>(g, "ACM classifiers", ACMClassifier.class);

	public Publication(BGraph g) {
		super(g);
		this.title = new StringNode(g);
	}

	@Override
	public String whatIsThis() {
		return "Publication: " + title.get();
	}

	@Override
	public String toString() {
		if (title != null && title.get() != null)
			return title.get();
		return "" + id();
	}
}
