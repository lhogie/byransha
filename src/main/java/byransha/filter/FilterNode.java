package byransha.filter;

import byransha.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Predicate;

public abstract class FilterNode extends BNode {

    protected FilterNode(BBGraph g) {
        super(g);
    }

    protected FilterNode(BBGraph g, int id) {
        super(g, id);
    }

    public abstract boolean filter(BNode node);

    public List<Class<? extends BNode>> getSupportedTypes() {
        return List.of();
    }

    public boolean supportsNodeType(Class<? extends BNode> nodeClass) {
        List<Class<? extends BNode>> supportedTypes = getSupportedTypes();
        if (supportedTypes.isEmpty()) {
            return true;
        }

        return supportedTypes
            .stream()
            .anyMatch(supportedType ->
                supportedType.isAssignableFrom(nodeClass)
            );
    }

    public Predicate<BNode> toPredicate() {
        return node -> {
            if (!supportsNodeType(node.getClass())) {
                return true;
            }

            try {
                return filter(node);
            } catch (Exception e) {
                System.err.println(
                    "Error applying filter " +
                    getClass().getSimpleName() +
                    " to node " +
                    node.id() +
                    ": " +
                    e.getMessage()
                );
                return true;
            }
        };
    }

    public void configure(ObjectNode config) {
        // Base implementation - subclasses can override
    }

    public abstract String getFilterDescription();

    @Override
    public String whatIsThis() {
        return (
            "A filter that can be applied to search results. " +
            getFilterDescription()
        );
    }

    @Override
    public String prettyName() {
        return getFilterDescription();
    }
}
