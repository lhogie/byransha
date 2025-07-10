package byransha;

import byransha.annotations.ListSettings;
import java.lang.reflect.Field;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import toools.reflect.Clazz;

public class SetNode<N extends BNode> extends PersistingNode {

    @Override
    public String whatIsThis() {
        return "SetNode containing " + l.size() + " elements.";
    }

    public SetNode(BBGraph db) {
        super(db);
    }

    public SetNode(BBGraph db, int id) {
        super(db, id);
    }

    @Override
    public String prettyName() {
        return "a set";
    }

    private final Set<N> l = ConcurrentHashMap.newKeySet();

    @Override
    public void forEachOut(BiConsumer<String, BNode> consumer) {
        int i = 0;
        for (N e : l) {
            if (e != null) {
                consumer.accept(i++ + ". " + e.prettyName(), e);
            } else {
                i++;
            }
        }
    }

    public void add(N n) {
        if (l.add(n) && n != null && graph != null) {
            invalidateOutsCache();
        }
        this.save(f -> {});
    }

    public void removeAll() {
        l.clear();

        invalidateOutsCache();
        this.save(f -> {});
    }

    public int size() {
        return l.size();
    }

    public boolean canAddNewNode() {
        return getListSettings().allowCreation();
    }

    public boolean isDropdown() {
        return getListSettings().displayAsDropdown();
    }

    private ListSettings getListSettings() {
        for (InLink inLink : ins()) {
            for (Field field : inLink.source().getClass().getDeclaredFields()) {
                if (field.getType().isAssignableFrom(SetNode.class)) {
                    ListSettings annotation = field.getAnnotation(
                        ListSettings.class
                    );
                    if (annotation != null) {
                        return annotation;
                    }
                }
            }
        }

        return new ListSettings() {
            @Override
            public boolean allowCreation() {
                return true;
            }

            @Override
            public boolean displayAsDropdown() {
                return false;
            }

            @Override
            public Class<
                ? extends java.lang.annotation.Annotation
            > annotationType() {
                return ListSettings.class;
            }
        };
    }

    public void remove(N p) {
        if (l.remove(p) && p != null && graph != null) {
            int i = 0;
            for (N n : l) {
                if (n != null && n.equals(p)) {
                    break;
                }
                i++;
            }

            invalidateOutsCache();
        }
        this.save(f -> {});
    }
}
