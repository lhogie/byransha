package byransha;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class FileNode extends PrimitiveValueNode<byte[]> {

    public StringNode title;

    public FileNode(BBGraph g, User creator) {
        super(g, creator);
        title = new StringNode(g, creator);
    }

    @Override
    public void fromString(String s, User user) {
        set(Base64.getDecoder().decode(s.getBytes(StandardCharsets.UTF_8)), user);
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
        if (title.get() == null || title.get().isEmpty()) {
            return "FileNode(unknown)";
        }
        return "File" + title.get();
    }

    @Override
    public String prettyName() {
        if (title.get() == null || title.get().isEmpty()) {
            return "FileNode(unknown)";
        }
        return title.get();
    }

    @Override
    public String getAsString() {
        if (get() == null) {
            return "";
        }
        return Base64.getEncoder().encodeToString(get());
    }
}
