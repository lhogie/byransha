package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.ListNode;
import byransha.StringNode;

public class Publication extends BusinessNode {
	public StringNode title;
	public ListNode<Person> authors;
	public ACMClassifier acmClassifier;

	public Publication(BBGraph g, StringNode title, ListNode<Person> authors) {
		super(g);
		this.title = title;
		this.authors = authors;
	}

	public Publication(BBGraph g, int id) {
		super(g, id);
	}

	public Publication(BBGraph g) {
		super(g);
		this.title = new StringNode(g);
	}

	@Override
	public String whatIsThis() {
		return "Publication: " + title.get();
	}

	@Override
	public String toString() {
		if (title == null) {
			return "null";
		}
		return title.get();
	}

	@Override
	public String prettyName() {
		return "publication " + title.get();
	}
}
