package byransha;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class FileNode extends ValuedNode<byte[]> {

    public StringNode title;

    public FileNode(BBGraph g) {
        super(g);
        title = BNode.create(g, StringNode.class);
    }

    public FileNode(BBGraph g, int id) {
        super(g, id);
        //title = BNode.create(g, StringNode.class);
    }

    @Override
    public void fromString(String s) {
        System.out.println("FileNode.fromString: " + s);
        set(Base64.getDecoder().decode(s.getBytes(StandardCharsets.UTF_8)));
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
