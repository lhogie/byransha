package byransha;

import java.lang.reflect.Field;

public class BooleanNode extends ValuedNode<Boolean> {

    public String name = "boolean";

    public BooleanNode(BBGraph db) {
        super(db);
    }

    public BooleanNode(BBGraph db, int id) {
        super(db, id);
    }

    @Override
    public String prettyName() {
        return (get() == null ? "null" : get().toString());
    }



    @Override
    public void fromString(String s) {
        set(Boolean.valueOf(s));
    }

    @Override
    public String whatIsThis() {
        return "a boolean";
    }

    @Override
    public void set(Boolean newValue) {
        super.set(newValue);
    }

    public void set(String fieldName, BNode parentNode, Boolean newValue) {
        BooleanNode node = graph.find(BooleanNode.class, n -> {
            return n.get() != null && n.get().equals(newValue);
        });
        if (node != null) parentNode.setField(fieldName, node);
        else super.set(newValue);
    }

    public void setName(String name) {
        this.name = name;
    }
}
