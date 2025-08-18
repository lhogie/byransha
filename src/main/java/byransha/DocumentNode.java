package byransha;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class DocumentNode extends PrimitiveValueNode<byte[]> implements UpdatableNode {
    public StringNode title;
    public StringNode mimeType;

    public DocumentNode(BBGraph g, User creator) {
        super(g, creator);
        title = new StringNode(g, creator);
        mimeType = new StringNode(g, creator);
        endOfConstructor();
    }

    public DocumentNode(BBGraph g, User creator, int id) {
        super(g, creator, id);
        endOfConstructor();
    }

    @Override
    public void fromString(String s, User user) {
        String mimeType = "text/plain";
        if (s.startsWith("data:image/jpeg;base64,")) {mimeType = "image/jpeg";}
        else if (s.startsWith("data:image/gif;base64,")){mimeType = "image/gif";}
        else if (s.startsWith("data:image/svg+xml;base64,")){mimeType = "image/svg+xml";}
        else if (s.startsWith("data:application/pdf;base64,")) {mimeType = "application/pdf";}
        this.mimeType.set(mimeType, user);

        set(Base64.getDecoder().decode(s.getBytes(StandardCharsets.UTF_8)), user);
    }

    @Override
    protected byte[] valueToBytes(byte[] v) {
        return v;
    }

    @Override
    protected byte[] bytesToValue(byte[] bytes, User user) {
        return bytes;
    }

    @Override
    public String whatIsThis() {
        return "a document of type " + mimeType;
    }

    @Override
    public String prettyName() {
        return "document " + title.get();
    }

    @Override
    public String getAsString() {
        if (get() == null) {
            return "";
        }
        return Base64.getEncoder().encodeToString(get());
    }


}
