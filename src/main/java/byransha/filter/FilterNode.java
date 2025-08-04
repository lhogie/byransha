package byransha.filter;

import byransha.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Predicate;

public abstract class FilterNode extends PersistingNode {

    public StringNode fieldPath;
    public BooleanNode enabled;

    protected FilterNode(BBGraph g) {
        super(g);
        fieldPath = g.create( StringNode.class);
        enabled = g.create( BooleanNode.class);
        enabled.set("enabled", this, true);
    }

    protected FilterNode(BBGraph g, int id) {
        super(g, id);
    }

    @Override
    protected void initialized() {
        super.initialized();
        if (fieldPath.get() == null) {
            fieldPath.set("");
        }
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

    protected Object getFieldValue(BNode node) {
        String path = fieldPath.get();
        if (path == null || path.trim().isEmpty()) {
            return node;
        }

        String[] pathParts = path.split("\\.");
        Object current = node;

        for (String part : pathParts) {
            if (current == null) {
                return null;
            }

            current = getFieldValueFromObject(current, part);
        }

        return current;
    }

    private Object getFieldValueFromObject(Object obj, String fieldName) {
        if (obj == null) {
            return null;
        }

        try {
            if (obj instanceof BNode bnode) {
                BNode[] outNodes = new BNode[1];
                bnode.forEachOut((name, outNode) -> {
                    if (name.equals(fieldName)) {
                        outNodes[0] = outNode;
                    }
                });

                if (outNodes[0] != null) {
                    return outNodes[0];
                }
            }

            Class<?> clazz = obj.getClass();
            while (clazz != null) {
                try {
                    Field field = clazz.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    return field.get(obj);
                } catch (NoSuchFieldException e) {
                    clazz = clazz.getSuperclass();
                }
            }
        } catch (Exception e) {
            System.err.println(
                "Error accessing field '" +
                fieldName +
                "' on object of type " +
                obj.getClass().getSimpleName() +
                ": " +
                e.getMessage()
            );
        }

        return null;
    }

    protected String valueToString(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof ValuedNode<?> valuedNode) {
            return valuedNode.getAsString();
        }

        if (value instanceof BNode bnode) {
            return bnode.prettyName();
        }

        return value.toString();
    }

    public Predicate<BNode> toPredicate() {
        return node -> {
            if (!enabled.get()) {
                return true;
            }

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
        if (config.has("fieldPath")) {
            fieldPath.set(config.get("fieldPath").asText());
        }
        if (config.has("enabled")) {
            enabled.set(config.get("enabled").asBoolean());
        }
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
        String desc = getFilterDescription();
        String path = fieldPath.get();
        if (path != null && !path.trim().isEmpty()) {
            return desc + " on field: " + path;
        }
        return desc;
    }
}
