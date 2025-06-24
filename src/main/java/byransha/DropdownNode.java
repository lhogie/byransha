package byransha;

import java.util.function.BiConsumer;

public class DropdownNode<N extends BNode> extends ValuedNode<N>{

    public N value;

    public DropdownNode(BBGraph db) {
        super(db);
    }

    public DropdownNode(BBGraph db, int id) {
        super(db, id);
    }

    @Override
    public void fromString(String s) {
        if (s == null || s.isEmpty()) {
            value = null;
        } else {
            var v = graph.findByID(Integer.parseInt(s));
            if (v == null) {
                throw new IllegalArgumentException("No node found with ID: " + s);
            }
            if (!(v instanceof BNode)) {
                throw new IllegalArgumentException("Node with ID " + s + " is not a valid BNode");
            }

            value = (N) v;
        }
    }

    @Override
    public String whatIsThis() {
        return "a dropdown node";
    }

    @Override
    public String prettyName() {
        return "a dropdown";
    }

    @Override
    public void forEachOut(BiConsumer<String, BNode> consumer) {
        if (value != null) {
            consumer.accept("Selected Value", value);
        }
    }
}
