package byransha;

import java.io.IOException;
import java.lang.reflect.Field;

public  class Out<V extends NotPrimitiveNode> extends PrimitiveValueNode<V> {
    Field f;

    public Out(BBGraph g, User user, InstantiationInfo ii) {
        super(g, user, ii, true);
        endOfConstructor();
    }

    public Out(BBGraph g, User user) {
        this(g, user, InstantiationInfo.persisting);
    }


    @Override
    protected void createOuts(User creator) {
        super.createOuts(creator);
    }

    @Override
    public void fromString(String s, User user) {
        set((V) g.findByID(Integer.parseInt(s)), user);
    }

    @Override
    protected byte[] valueToBytes(V v) throws IOException {
        return String.valueOf(v.id()).getBytes();
    }

    @Override
    protected V bytesToValue(byte[] bytes, User user) throws IOException {
        return (V) g.findByID(Integer.valueOf(String.valueOf(bytes)));
    }


    @Override
    public String whatIsThis() {
        return "an arc";
    }

    @Override
    public String prettyName() {
        return "arc: " + get();
    }

}
