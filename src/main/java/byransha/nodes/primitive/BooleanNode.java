package byransha.nodes.primitive;

import byransha.BBGraph;
import byransha.nodes.system.User;

import java.io.IOException;

public class BooleanNode extends PrimitiveValueNode<Boolean> {

    public BooleanNode(BBGraph g, User creator, boolean v) {
        super(g, creator);
        set(v, creator);
    }

    @Override
    public boolean canCreate(User creator) {
        return creator == g.systemUser();
    }

    @Override
    public String prettyName() {
        return get().toString();
    }

    @Override
    protected Boolean bytesToValue(byte[] bytes, User user) throws IOException {
        if (bytes.length != 1) throw new IOException("Invalid byte array length for BooleanNode: " + bytes.length);
        return bytes[0] != 0;
    }

    @Override
    protected byte[] valueToBytes(Boolean aBoolean) throws IOException {
        return new byte[]{(byte) (aBoolean ? 1 : 0)};
    }

    @Override
    public void fromString(String s, User user) {
        set(Boolean.valueOf(s), user);
    }

    @Override
    public String whatIsThis() {
        return "a boolean value, (true of false)";
    }
}
