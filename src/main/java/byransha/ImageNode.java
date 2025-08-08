package byransha;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class ImageNode extends ValuedNode<byte[]> {

    public StringNode title;

    public ImageNode(BBGraph g) {
        super(g);
        title = g.create(StringNode.class);
    }

    public ImageNode(BBGraph g, int id) {
        super(g, id);
        title = g.create(StringNode.class);
    }

    @Override
    public String prettyName() {
        if (title.get() == null || title.get().isEmpty()) {
            return "ImageNode(unknown)";
        }
        return title.get();
    }

    @Override
    public void fromString(String s) {
        set(Base64.getDecoder().decode(s.getBytes(StandardCharsets.UTF_8)));
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
    protected void fromBytes(byte[] bytes) {
        set(bytes);
    }

    @Override
    public String whatIsThis() {
        return "an image";
    }
}
