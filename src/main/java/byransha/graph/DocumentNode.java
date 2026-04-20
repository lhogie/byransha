package byransha.graph;

import byransha.nodes.primitive.MimeTypeNode;
import byransha.nodes.primitive.StringNode;

public class DocumentNode extends BNode {
	public RawDataNode data;
	public StringNode title;
	public MimeTypeNode mimeType;

	public DocumentNode(BNode parent) {
		super(parent);
		data = new RawDataNode(parent);
		title = new StringNode(parent);
		mimeType = new MimeTypeNode(g());
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
