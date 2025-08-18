package byransha;

import java.util.LinkedHashMap;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * A dynamic node that acts as a shortcut to other nodes in the graph.
 * <p>
 * Example usage:
 * <pre><code>
 * pays = BNode.create(g, ShortcutNode.class);
 * pays.withOutsSupplier(() -> {
 *    LinkedHashMap<String, BNode> connected = new LinkedHashMap<>();
 *    graph
 *        .findAll(Country.class, p -> p.deleted == false)
 *        .stream()
 *        .forEach(node -> connected.put(node.prettyName(), node));
 *    return connected;
 * });
 *
 * </code></pre>
 */
public class ShortcutNode extends BNode {

    private Supplier<LinkedHashMap<String, BNode>> outsSupplier;

    public ShortcutNode(BBGraph g, User creator) {
        super(g, creator);
        this.outsSupplier = LinkedHashMap::new;
        endOfConstructor();
    }

    public ShortcutNode(BBGraph g, User creator, int id) {
        super(g, creator, id);
        this.outsSupplier = LinkedHashMap::new;
    }

    public ShortcutNode withOutsSupplier(
            Supplier<LinkedHashMap<String, BNode>> supplier
    ) {
        this.outsSupplier = supplier != null ? supplier : LinkedHashMap::new;
        return this;
    }

    @Override
    public LinkedHashMap<String, BNode> outs() {
        try {
            LinkedHashMap<String, BNode> dynamicOuts = outsSupplier.get();
            return dynamicOuts != null
                ? new LinkedHashMap<>(dynamicOuts)
                : new LinkedHashMap<>();
        } catch (Exception e) {
            System.err.println(
                "Error fetching dynamic outs for ShortcutNode " +
                id() +
                ": " +
                e.getMessage()
            );
            return new LinkedHashMap<>();
        }
    }

    @Override
    public void forEachOutField(BiConsumer<String, BNode> consumer) {
        try {
            LinkedHashMap<String, BNode> dynamicOuts = outsSupplier.get();
            if (dynamicOuts != null) {
                dynamicOuts.forEach(consumer);
            }
        } catch (Exception e) {
            System.err.println(
                "Error in forEachOut for ShortcutNode " +
                id() +
                ": " +
                e.getMessage()
            );
        }
    }

    @Override
    protected void invalidateOutsCache() {}

    @Override
    public String prettyName() {
        return "shortcut";
    }


    @Override
    public String whatIsThis() {
        return "a shortcut";
    }
}
