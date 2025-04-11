package byransha.labmodel.model.v0;

import byransha.BNode;
import byransha.BBGraph;
import byransha.ListNode;
import byransha.StringNode;

public class Publication extends BNode {
	public StringNode title = new StringNode(graph, null);

	private ListNode<Person> authors = new ListNode<>(graph);

  
  public Publication(BBGraph g) {
		super(g);
	}

	@Override
	public String whatIsThis() {
		return "Publication: " + title.get();
	}

	@Override
	public String toString() {
		return title.get();
	}

	@Override
	public String prettyName() {
		return "publication " + title.get();
	}
}
