package byransha.nodes.lab.model.v0;

import byransha.graph.BBGraph;
import byransha.nodes.primitive.IntNode;
import byransha.nodes.primitive.ListNode;
import byransha.nodes.primitive.StringNode;
import byransha.nodes.system.User;

public class Publication extends BusinessNode {
	public StringNode title;
	public ListNode<Person> authors;
	public IntNode halID;

	public ACMClassifier acmClassifier;

	public Publication(BBGraph g, User creator) {
		super(g, creator);
		this.title = new StringNode(g, creator);
		this.authors = new ListNode<>(g, creator);
		this.acmClassifier = new ACMClassifier(g, creator);
	}

	@Override
	public String whatIsThis() {
		return "Publication: " + title.get();
	}

	@Override
	public String toString() {
		if (title == null) {
			return "Publication: " + id();
		}
		return title.get();
	}

	@Override
	public String prettyName() {
		if (title != null && title.get() != null)
			return title.get();
		return null;
	}
}
