package byransha.nodes.lab;

import byransha.graph.BGraph;
import byransha.graph.action.list.ListNode;
import byransha.nodes.primitive.LongNode;
import byransha.nodes.primitive.StringNode;

public class Publication extends BusinessNode {
	public StringNode title;
	public ListNode<Person> authors;
	public final StringNode halID = new StringNode(g, null, "^hal-\\d+$");

	public ListNode<ACMClassifier> acmClassifier;

	public Publication(BGraph g) {
		super(g);
		this.title = new StringNode(g);
		this.authors = new ListNode<>(g, "author(s)");
		this.acmClassifier = new ListNode<>(g, "ACM classifiers");
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
