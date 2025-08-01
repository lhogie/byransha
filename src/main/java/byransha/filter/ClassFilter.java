package byransha.filter;

import byransha.BBGraph;
import byransha.BNode;
import byransha.BooleanNode;
import byransha.ListNode;
import byransha.StringNode;
import byransha.annotations.ListOptions;
import byransha.labmodel.model.v0.BusinessNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.List;

public class ClassFilter extends FilterNode {

    @ListOptions(
        type = ListOptions.ListType.DROPDOWN,
        elementType = ListOptions.ElementType.STRING,
        allowCreation = false,
        source = ListOptions.OptionsSource.PROGRAMMATIC
    )
    public ListNode<StringNode> targetClass;

    public BooleanNode includeSubclasses;

    public ClassFilter(BBGraph g) {
        super(g);
        targetClass = BNode.create(g, ListNode.class);
        includeSubclasses = BNode.create(g, BooleanNode.class);
        includeSubclasses.set("includeSubclasses", this, true);
    }

    public ClassFilter(BBGraph g, int id) {
        super(g, id);
    }

    @Override
    protected void initialized() {
        super.initialized();

        populateClassOptions();
    }

    private void populateClassOptions() {
        var allClasses = new ArrayList<String>();

        graph.forEachNode(node -> {
            if (
                node instanceof BusinessNode &&
                !node.deleted &&
                allClasses
                    .stream()
                    .noneMatch(s -> s.equals(node.getClass().getSimpleName()))
            ) {
                allClasses.add(node.getClass().getSimpleName());
            }
        });

        allClasses.sort(String::compareToIgnoreCase);

        targetClass.setStaticOptions(allClasses);
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
    public void configure(ObjectNode config) {
        super.configure(config);

        if (config.has("targetClass")) {
            targetClass.removeAll();
            StringNode classNode = BNode.create(graph, StringNode.class);
            classNode.set(config.get("targetClass").asText());
            targetClass.add(classNode);
        }

        if (config.has("includeSubclasses")) {
            includeSubclasses.set(config.get("includeSubclasses").asBoolean());
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
}
