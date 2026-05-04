package byransha.graph;

import byransha.nodes.primitive.StringNode;
import byransha.nodes.primitive.URLNode;

public class DocumentNode extends BNode {
	@ShowInKishanView
	public URLNode url = new URLNode(this, null);
	@ShowInKishanView
	public StringNode name = new StringNode(this);
	@ShowInKishanView
	public BNode relatedTo;

	public DocumentNode(BNode parent) {
		super(parent);
	}

	@Override
	public String whatIsThis() {
		return "a document";
	}

	@Override
	public String toString() {
		return name + " for " + relatedTo;
	}
}
