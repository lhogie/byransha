package byransha.nodes.primitive;

import byransha.BBGraph;
import byransha.nodes.system.User;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class StringNode extends PrimitiveValueNode<String> {

    public StringNode(BBGraph db, User creator) {
        super(db, creator);
    }


    public StringNode(BBGraph g, User creator, String init) {
        this(g, creator);
        set(init, creator);
    }

    @Override
    protected byte[] valueToBytes(String s) throws IOException {
        return s.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    protected String bytesToValue(byte[] bytes, User user) throws IOException {
        return new String(bytes, StandardCharsets.UTF_8);
    }

    @Override
    public String prettyName() {
        return get();
    }

    @Override
    public void fromString(String s, User creator) {
        set(s, creator);
    }

    @Override
    public String whatIsThis() {
        return "a sequence of characters";
    }
}
