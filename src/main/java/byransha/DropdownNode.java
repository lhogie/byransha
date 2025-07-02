package byransha;

import java.util.function.BiConsumer;

import byransha.annotations.ListSettings;

import java.lang.reflect.Field;

public class DropdownNode<N extends BNode> extends ValuedNode<N>{

    public N value;

    public DropdownNode(BBGraph db) {
        super(db);
    }

    public DropdownNode(BBGraph db, int id) {
        super(db, id);
    }

    public boolean canAddNewNode() {
        return getListSettings().allowCreation();
    }

    public boolean isDropdown() {
        return getListSettings().displayAsDropdown();
    }

    private ListSettings getListSettings() {
        for (InLink inLink : ins()) {
            for (Field field : inLink.source.getClass().getDeclaredFields()) {
                if (field.getType().isAssignableFrom(DropdownNode.class)) {
                    ListSettings annotation = field.getAnnotation(ListSettings.class);
                    if (annotation != null) {
                        return annotation;
                    }
                }
            }
        }
        // Return default settings if no annotation is found
        return new ListSettings() {
            @Override
            public boolean allowCreation() {
                return false;
            }

            @Override
            public boolean displayAsDropdown() {
                return true;
            }

            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() {
                return ListSettings.class;
            }
        };
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
