package byransha.graph;

import byransha.nodes.primitive.MimeTypeNode;
import byransha.nodes.primitive.StringNode;

public class DocumentNode extends BNode {
	public RawDataNode data;
	public StringNode title;
	public MimeTypeNode mimeType;

	public DocumentNode(BGraph g) {
		super(g);
		data = new RawDataNode(g);
		title = new StringNode(g);
		mimeType = new MimeTypeNode(g);
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
