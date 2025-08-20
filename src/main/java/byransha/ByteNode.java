package byransha;

import java.util.Base64;

public class ByteNode extends ValuedNode<byte[]> {
    public ByteNode(BBGraph g, User user, InstantiationInfo ii) {
        super(g, user, ii, false);
        endOfConstructor();
    }

    @Override
    protected void createOuts(User creator) {
        super.createOuts(creator);
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
    public String getAsString() {
        if (get() == null) {
            return "";
        }
        return Base64.getEncoder().encodeToString(get());
    }


    @Override
    public String whatIsThis() {
        return "raw data";
    }

    @Override
    public String prettyName() {
        return get().length +  " bytes";
    }
}
