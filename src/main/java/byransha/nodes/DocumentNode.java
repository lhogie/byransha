package byransha.nodes;

import byransha.BBGraph;
import byransha.nodes.system.User;
import byransha.nodes.primitive.ByteNode;
import byransha.nodes.primitive.MimeTypeNode;
import byransha.nodes.primitive.StringNode;

public class DocumentNode extends BNode {
    public ByteNode data;
    public StringNode title;
    public MimeTypeNode mimeType;

    public DocumentNode(BBGraph g, User creator) {
        super(g, creator);
        data = new ByteNode(g, creator);
        title = new StringNode(g, creator);
        mimeType = new MimeTypeNode(g, creator);
    }


    @Override
    public String whatIsThis() {
        return "a document of type " + mimeType;
    }

    @Override
    public String prettyName() {
        return "document " + title.get();
    }
}
