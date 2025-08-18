package byransha;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.function.Consumer;

public  class Out<V extends BNode> extends PrimitiveValueNode<V> {
    Field f;

    public Out(BBGraph g, User user) {
        super(g, user);
        endOfConstructor();
    }

    public Out(BBGraph g, User user, int id) {
        super(g,  user, id);
        endOfConstructor();
    }

    @Override
    public void fromString(String s, User user) {
        set((V) graph.findByID(Integer.parseInt(s)), user);
    }

    @Override
    protected byte[] valueToBytes(V v) throws IOException {
        return String.valueOf(v.id()).getBytes();
    }

    @Override
    protected V bytesToValue(byte[] bytes, User user) throws IOException {
        return (V) graph.findByID(Integer.valueOf(String.valueOf(bytes)));
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
