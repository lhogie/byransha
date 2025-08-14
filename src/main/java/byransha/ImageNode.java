package byransha;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class ImageNode extends PrimitiveValueNode<byte[]> {

    public StringNode title;

    public ImageNode(BBGraph g, User creator) {
        super(g, creator);
        title = new StringNode(g, creator);
    }

    public ImageNode(BBGraph g, User creator, int id) {
        super(g, creator, id);
        title = new StringNode(g, creator);
    }

    @Override
    public String prettyName() {
        if (title.get() == null || title.get().isEmpty()) {
            return "ImageNode(unknown)";
        }
        return title.get();
    }

    @Override
    public void fromString(String s, User user) {
        set(Base64.getDecoder().decode(s.getBytes(StandardCharsets.UTF_8)), user);
    }

    @Override
    public String getAsString() {
        if (get() == null) {
            return "";
        }
        return Base64.getEncoder().encodeToString(get());
    }

    @Override
    protected byte[] toBytes(byte[] v) {
        return v;
    }

    @Override
    protected void fromBytes(byte[] bytes, User user) {
        set(bytes, user);
    }

    @Override
    public String whatIsThis() {
        return "an image";
    }
}
