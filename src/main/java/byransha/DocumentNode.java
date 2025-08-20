package byransha;

import byransha.labmodel.model.v0.NodeBuilder;

import java.util.function.Function;

public class DocumentNode extends NotPrimitiveNode {
    public ByteNode data;
    public StringNode title;
    public MimeTypeNode mimeType;

    public DocumentNode(BBGraph g, User creator, InstantiationInfo ii) {
        super(g, creator, ii);
        endOfConstructor();
    }

    @Override
    protected void createOuts(User creator) {
        data = new ByteNode(g, creator, InstantiationInfo.persisting);
        title = new StringNode(g, creator, InstantiationInfo.persisting);
        mimeType = new MimeTypeNode(g, creator, InstantiationInfo.persisting);
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
