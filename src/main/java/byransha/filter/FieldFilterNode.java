package byransha.filter;

import byransha.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.lang.reflect.Field;

public abstract class FieldFilterNode extends FilterNode {

    public StringNode fieldPath;

    protected FieldFilterNode(BBGraph g, User creator) {
        super(g, creator);
        fieldPath = new StringNode(g, creator);
    }

    protected FieldFilterNode(BBGraph g, User creator, int id) {
        super(g, creator, id);
    }

    @Override
    protected void initialized(User user) {
        super.initialized(user);
        if (fieldPath.get() == null) {
            fieldPath.set("", user);
        }
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

    @Override
    public void configure(ObjectNode config, User user) {
        super.configure(config, user);
        if (config.has("fieldPath")) {
            fieldPath.set(config.get("fieldPath").asText(), user);
        }
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
