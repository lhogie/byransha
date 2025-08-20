package byransha;

import java.io.IOException;

public class BooleanNode extends PrimitiveValueNode<Boolean> {

    public BooleanNode(BBGraph g, User user, InstantiationInfo ii) {
        super(g, user, ii);
        endOfConstructor();
    }

    @Override
    public String prettyName() {
        return (get() == null ? "null" : get().toString());
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
        return "a boolean";
    }

    @Override
    public void set(Boolean newValue, User user) {
        super.set(newValue, user);
    }

    public void set(String fieldName, BNode parentNode, Boolean newValue, User user) {
        BooleanNode node = g.find(BooleanNode.class, n -> {
            return n.get() != null && n.get().equals(newValue);
        });
        if (node != null) parentNode.setField(fieldName, node);
        else super.set(newValue, user);
    }
}
