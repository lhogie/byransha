package byransha.filter;

import byransha.*;
import byransha.annotations.ListOptions;
import byransha.labmodel.model.v0.BusinessNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ClassFilter extends FieldFilterNode {

    @ListOptions(
        type = ListOptions.ListType.DROPDOWN,
        allowCreation = false
    )
    public ListNode<Cluster> targetClass;

    public BooleanNode includeSubclasses;

    public ClassFilter(BBGraph g, User creator, InstantiationInfo ii) {
        super(g, creator, ii);
        endOfConstructor();
    }

    @Override
    protected void createOuts(User creator) {
        super.createOuts(creator);
        targetClass = new ListNode(g, creator, InstantiationInfo.persisting);
        includeSubclasses = new BooleanNode(g, creator, InstantiationInfo.persisting);
        includeSubclasses.set(true, creator);
    }

    @Override
    protected void nodeConstructed(User user) {
        super.nodeConstructed(user);
        populateClassOptions();
    }

    private void populateClassOptions() {
        var allClasses = new HashSet<Class<? extends BNode>>();

        g.forEachNode(node -> {
            if (node instanceof BusinessNode) {
                allClasses.add(node.getClass());
            }
        });

        var l = new ArrayList<>(
            allClasses
                .stream()
                .map(e -> e.getSimpleName())
                .toList()
        );
        l.sort(String::compareToIgnoreCase);

        targetClass.setStaticOptions(l);
    }
    
    @Override
    public boolean hasFilledValues() {
        try {
            if (targetClass.size() == 0) {
                return false;
            }
            Cluster cl = targetClass.get(0);
            String selectedClass = cl != null ? cl.typeOfCluster.getSimpleName() : null;
            return selectedClass != null && !selectedClass.trim().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
    

    @Override
    public boolean filter(BNode node) {
        Cluster cl = targetClass.get(0);
        String selectedClass= cl != null ? cl.typeOfCluster.getSimpleName() : null;

        if ( selectedClass == null || selectedClass.trim().isEmpty()) {
//            System.out.println("No class selected, allowing all nodes.");
            return true;
        }

        String nodeClassName = node.getClass().getSimpleName();

        if (includeSubclasses.get()) {
            try {
                Class<?> nodeClass = node.getClass();
                Class<?> targetClassObj = findClassBySimpleName(selectedClass);

                if (targetClassObj != null) {
                    return targetClassObj.isAssignableFrom(nodeClass);
                }
            } catch (Exception e) {
                System.err.println(
                    "Error checking class hierarchy for " +
                    selectedClass +
                    ": " +
                    e.getMessage()
                );
            }
        }

        return nodeClassName.equalsIgnoreCase(selectedClass);
    }

    private Class<?> findClassBySimpleName(String simpleName) {
        String[] packages = {
            "byransha.labmodel.model.v0.",
            "byransha.labmodel.model.gitMind.",
            "byransha.",
            "",
        };

        for (String packageName : packages) {
            try {
                return Class.forName(packageName + simpleName);
            } catch (ClassNotFoundException e) {}
        }

        return null;
    }

    @Override
    public List<Class<? extends BNode>> getSupportedTypes() {
        return List.of();
    }

    @Override
    public void configure(ObjectNode config, User user) {
        super.configure(config, user);

        if (config.has("targetClass")) {

            // Auto-enable when a class is selected
            String selectedClass = config.get("targetClass").asText();
            if (selectedClass != null && !selectedClass.trim().isEmpty()) {
                enabled.set(true, user);
            }
        }

        if (config.has("includeSubclasses")) {
            includeSubclasses.set(config.get("includeSubclasses").asBoolean(), user);
        }
    }

    @Override
    public String getFilterDescription() {
        String selectedClass = targetClass.getSelected();

        if (selectedClass == null || selectedClass.trim().isEmpty()) {
            return "Class filter (no class selected)";
        }

        String subclassText = includeSubclasses.get()
            ? " (including subclasses)"
            : " (exact match)";
        return "Class: " + selectedClass + subclassText;
    }

    @Override
    public String prettyName() {
        if (targetClass == null) {
            return "Class Filter (unconfigured)";
        }

        String selectedClass = targetClass.getSelected();

        if (selectedClass == null || selectedClass.trim().isEmpty()) {
            return "Class Filter";
        }

        return "Class: " + selectedClass;
    }

    public void refreshClassOptions() {
        populateClassOptions();
    }

    public void setTargetClass(String className, User user) {
        targetClass.removeAll();
        if (className != null && !className.trim().isEmpty()) {
            // Auto-enable when ar class is selected
            enabled.set(true, user);
        } else {
            // Auto-disable when no class is selected
            enabled.set(false, user);
        }
    }
}
