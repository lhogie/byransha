package byransha;

import java.util.function.BiConsumer;

public class DropdownNode<N extends BNode> extends PersistingNode{

    public N value;

    public DropdownNode(BBGraph db) {
        super(db);
    }

    public DropdownNode(BBGraph db, int id) {
        super(db, id);
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

    public void setValue(N value) {
        this.value = value;
        this.save(f -> {});
    }

    public N getValue() {
        return value;
    }

}
