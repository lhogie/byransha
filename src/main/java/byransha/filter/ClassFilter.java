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
        elementType = ListOptions.ElementType.STRING,
        allowCreation = false,
        source = ListOptions.OptionsSource.PROGRAMMATIC
    )
    public ListNode<StringNode> targetClass;

    public BooleanNode includeSubclasses;

    public ClassFilter(BBGraph g, User creator) {
        super(g, creator);
        targetClass = new ListNode(g, creator);
        includeSubclasses = new BooleanNode(g, creator);
        includeSubclasses.set("includeSubclasses", this, true, creator);
    }

    public ClassFilter(BBGraph g, User creator, int id) {
        super(g, creator, id);
    }

    @Override
    protected void initialized(User user) {
        super.initialized(user);
        populateClassOptions();
    }

    private void populateClassOptions() {
        var allClasses = new HashSet<Class<? extends BNode>>();

        graph.forEachNode(node -> {
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
    public boolean filter(BNode node) {
        String selectedClass = targetClass.getSelected();

        if (selectedClass == null || selectedClass.trim().isEmpty()) {
            System.out.println("No class selected, allowing all nodes.");
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

        System.out.println(
            "Checking class: " +
            nodeClassName +
            " against target: " +
            selectedClass
        );

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
            targetClass.removeAll();
            StringNode classNode = new StringNode(graph, creator);
            classNode.set(config.get("targetClass").asText(), user);
            targetClass.add(classNode, user);

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
            StringNode classNode = new StringNode(graph, creator);
            classNode.set(className, user);
            targetClass.add(classNode, user);
            // Auto-enable when ar class is selected
            enabled.set(true, user);
        } else {
            // Auto-disable when no class is selected
            enabled.set(false, user);
        }
    }
}
