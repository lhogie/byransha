package byransha;

import java.io.File;
import java.util.function.Consumer;

public class BooleanNode extends PrimitiveValueNode<Boolean> {

    public BooleanNode(BBGraph g, User user) {
        super(g, user);
        endOfConstructor();
    }

    public BooleanNode(BBGraph g, User user, int id) {
        super(g ,user, id);
        endOfConstructor();
    }

    @Override
    public String prettyName() {
        return (get() == null ? "null" : get().toString());
    }

    @Override
    public void saveValue(ValueHistoryEntry<Boolean> e, Consumer<File> writingFiles){

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
        BooleanNode node = graph.find(BooleanNode.class, n -> {
            return n.get() != null && n.get().equals(newValue);
        });
        if (node != null) parentNode.setField(fieldName, node);
        else super.set(newValue, user);
    }
}
