package byransha.graph;

import byransha.nodes.primitive.MimeTypeNode;
import byransha.nodes.primitive.StringNode;
import byransha.nodes.primitive.URLNode;

public class DocumentNode extends BNode {
	public URLNode url = new URLNode(this, null);
	public StringNode title= new StringNode(this);
	public MimeTypeNode mimeType = new MimeTypeNode(this);

	public DocumentNode(BNode parent) {
		super(parent);
	}

	@Override
	public String whatIsThis() {
		return "a document of type " + mimeType;
	}

	@Override
	public String toString() {
		return title.get();
	}
}
