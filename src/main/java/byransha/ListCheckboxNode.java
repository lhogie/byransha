package byransha;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;

public class ListCheckboxNode extends PersistingNode {
    public final List<BooleanNode> l = new CopyOnWriteArrayList<>();

    @Override
    public String whatIsThis() {
        return "ListCheckboxNode containing " + l.size() + " elements.";
    }

    public ListCheckboxNode(BBGraph db) {
        super(db);
    }

    public ListCheckboxNode(BBGraph db, int id) {
        super(db, id);
    }

    @Override
    public String prettyName() {
        return "a set of checkboxes";
    }

    @Override
    public void forEachOut(BiConsumer<String, BNode> consumer) {
        int i = 0;
        for (BooleanNode e : l) {
            if (e != null) {
                consumer.accept(i++ + ". " + e.prettyName(), e);
            } else {
                i++;
            }
        }
    }

    public void addOption(String n) {
        var b = BNode.create(graph, BooleanNode.class);
        b.setName(n);
        l.add(b);
        this.save(f -> {});
    }

    public void addOptions(String... newOptions) {
        for (String option : newOptions) {
            var b = BNode.create(graph, BooleanNode.class);
            b.setName(option);
            l.add(b);
        }
        this.save(f -> {});
    }
}
