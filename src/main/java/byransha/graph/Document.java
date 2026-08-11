package byransha.graph;

import byransha.ID;
import byransha.primitive.StringNode;
import byransha.primitive.URLNode;

public class Document extends Element {
	@ShowInKishanView
	public URLNode url = new URLNode(this, id().augmentWith("url"), null);
	@ShowInKishanView
	public StringNode name = new StringNode(this, id().augmentWith("name"), null, ".+");
	@ShowInKishanView
	public Element relatedTo;

	public Document(Element parent, ID id) {
		super(parent, id);
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
