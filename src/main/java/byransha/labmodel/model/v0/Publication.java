package byransha.labmodel.model.v0;

import byransha.BNode;
import byransha.BBGraph;
import byransha.ListNode;
import byransha.StringNode;

public class Publication extends BNode {
	public StringNode title;

	private ListNode<Person> authors;

  
  	public Publication(BBGraph g) {
		super(g);
		title = new StringNode(g, null);
		authors = new ListNode<>(g);
	}

	public Publication(BBGraph g, int id) {
		super(g, id);
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
