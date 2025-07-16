package byransha;

import byransha.annotations.ListOptions;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

public class ListNode<T> extends PersistingNode {

    private final Set<T> elements = ConcurrentHashMap.newKeySet();
    private List<String> staticOptions = new ArrayList<>();
    private ListOptions.ListType listType;
    private ListOptions.OptionsSource optionsSource;
    private Class<?> elementType;

    private static final Map<Integer, ListOptions> optionsCache =
        new ConcurrentHashMap<>();
    private static final Random RANDOM = new Random();

    public ListNode(BBGraph db) {
        super(db);
    }

    public ListNode(BBGraph db, int id) {
        super(db, id);
    }

    @Override
    public String whatIsThis() {
        return (
            getListOptions().type().name().toLowerCase() +
            " containing " +
            size() +
            " elements"
        );
    }

    @Override
    public String prettyName() {
        return switch (getListOptions().type()) {
            case LIST -> "a list";
            case CHECKBOX -> "a set of checkboxes";
            case DROPDOWN -> "a dropdown";
            case RADIO -> "a radio button group";
        };
    }

    @Override
    public void forEachOut(BiConsumer<String, BNode> consumer) {
        int i = 0;
        for (T element : getElements()) {
            if (element instanceof BNode bNode) {
                consumer.accept(i++ + ". " + bNode.prettyName(), bNode);
            }
        }
    }

    public void add(T element) {
        if (element == null) return;

        ListOptions options = getListOptions();

        if (options.maxItems() > 0 && size() >= options.maxItems()) {
            throw new IllegalStateException(
                "Maximum number of items reached: " + options.maxItems()
            );
        }

        switch (listType) {
            case LIST:
            case CHECKBOX:
            case DROPDOWN:
            case RADIO:
                elements.add(element);
                invalidateOutsCache();
                save(f -> {});
                break;
        }
    }

    public void remove(T element) {
        if (element == null) return;

        boolean removed = false;
        switch (listType) {
            default:
                removed = elements.remove(element);
        }

        if (removed) {
            invalidateOutsCache();
            save(f -> {});
        }
    }

    public void select(int index) {
        if (index < 0 || index >= staticOptions.size()) {
            throw new IndexOutOfBoundsException(
                "Index out of bounds: " + index
            );
        }

        var option = staticOptions.get(index);
        var existingElement = elements.stream().findFirst();

        if (existingElement.isPresent()) {
            StringNode existingNode = (StringNode) existingElement.get();
            existingNode.set(option);
        } else {
            var n = BNode.create(graph, StringNode.class);
            n.set(option);
            add((T) n);
        }
    }

    public int getSelectedIndex() {
        if (elements.isEmpty()) {
            return -1;
        }

        String selected = elements
            .stream()
            .filter(e -> e instanceof StringNode)
            .map(e -> (StringNode) e)
            .map(StringNode::get)
            .findFirst()
            .orElse(null);

        return staticOptions.indexOf(selected);
    }

    public String getSelected() {
        if (elements.isEmpty()) {
            return null;
        }

        return elements
            .stream()
            .filter(e -> e instanceof StringNode)
            .map(e -> (StringNode) e)
            .map(StringNode::get)
            .findFirst()
            .orElse(null);
    }

    public void removeAll() {
        elements.clear();
        invalidateOutsCache();
        save(f -> {});
    }

    public List<T> getElements() {
        return List.copyOf(elements);
    }

    public int size() {
        return elements.size();
    }

    public T get(int index) {
        if (index < 0 || index >= elements.size()) {
            return null;
        }
        return elements.stream().skip(index).findFirst().orElse(null);
    }

    public T random() {
        if (elements.isEmpty()) {
            return null;
        }
        return elements
            .stream()
            .skip(RANDOM.nextInt(elements.size()))
            .findFirst()
            .orElse(null);
    }

    public boolean canAddNewNode() {
        ListOptions options = getListOptions();
        return (
            options.allowCreation() &&
            options.source() == ListOptions.OptionsSource.DYNAMIC
        );
    }

    public boolean isDropdown() {
        return getListOptions().displayAsDropdown();
    }

    public boolean allowMultiple() {
        return getListOptions().allowMultiple();
    }

    public ListOptions.ListType getListType() {
        return getListOptions().type();
    }

    public ListOptions.OptionsSource getOptionsSource() {
        return getListOptions().source();
    }

    public List<String> getStaticOptions() {
        return List.copyOf(staticOptions);
    }

    public void setStaticOptions(List<String> options) {
        if (
            getListOptions().source() == ListOptions.OptionsSource.PROGRAMMATIC
        ) {
            this.staticOptions = new ArrayList<>(options);
            save(f -> {});
        }
    }

    public void addStaticOption(String option) {
        if (
            getListOptions().source() == ListOptions.OptionsSource.PROGRAMMATIC
        ) {
            if (!staticOptions.contains(option)) {
                staticOptions.add(option);
                save(f -> {});
            }
        }
    }

    public List<String> getOptionsList() {
        return staticOptions;
    }

    public void addString(String value) {
        add((T) value);
    }

    public void addInteger(Integer value) {
        add((T) value);
    }

    public void addBoolean(Boolean value) {
        add((T) value);
    }

    public void addDouble(Double value) {
        add((T) value);
    }

    public List<String> getStrings() {
        return elements
            .stream()
            .filter(e -> e instanceof String)
            .map(e -> (String) e)
            .toList();
    }

    public List<Integer> getIntegers() {
        return elements
            .stream()
            .filter(e -> e instanceof Integer)
            .map(e -> (Integer) e)
            .toList();
    }

    private ListOptions getListOptions() {
        if (this != null && optionsCache.containsKey(id())) {
            return optionsCache.get(id());
        }

        for (InLink inLink : ins()) {
            BNode sourceNode = inLink.source();

            for (Field field : sourceNode.getClass().getDeclaredFields()) {
                if (field.getType().isAssignableFrom(ListNode.class)) {
                    try {
                        field.setAccessible(true);
                        Object fieldValue = field.get(sourceNode);
                        if (fieldValue == this) {
                            ListOptions annotation = field.getAnnotation(
                                ListOptions.class
                            );
                            if (annotation != null) {
                                this.listType = annotation.type();
                                this.optionsSource = annotation.source();
                                this.staticOptions = Arrays.asList(
                                    annotation.staticOptions()
                                );
                                this.elementType = annotation
                                    .elementType()
                                    .getClass();
                                this.listType = annotation.type();
                                this.optionsSource = annotation.source();
                                if (
                                    annotation.source() ==
                                    ListOptions.OptionsSource.STATIC
                                ) {
                                    this.staticOptions = Arrays.asList(
                                        annotation.staticOptions()
                                    );
                                }
                                optionsCache.put(id(), annotation);
                                return annotation;
                            }
                        } else {
                            System.out.println(
                                "Skipping field: " +
                                field.getName() +
                                " in " +
                                sourceNode.getClass().getSimpleName() +
                                " (not matching this ListNode)"
                            );
                        }
                    } catch (IllegalAccessException e) {}
                }
            }
        }

        ListOptions defaultOptions = new ListOptions() {
            @Override
            public ListType type() {
                return ListType.LIST;
            }

            @Override
            public OptionsSource source() {
                return OptionsSource.DYNAMIC;
            }

            @Override
            public String[] staticOptions() {
                return new String[0];
            }

            @Override
            public boolean allowCreation() {
                return true;
            }

            @Override
            public boolean displayAsDropdown() {
                return false;
            }

            @Override
            public boolean allowMultiple() {
                return false;
            }

            @Override
            public int maxItems() {
                return 0;
            }

            @Override
            public int minItems() {
                return 0;
            }

            @Override
            public ElementType elementType() {
                return ElementType.BNODE;
            }

            @Override
            public Class<
                ? extends java.lang.annotation.Annotation
            > annotationType() {
                return ListOptions.class;
            }
        };

        this.listType = defaultOptions.type();
        this.optionsSource = defaultOptions.source();
        this.staticOptions = Arrays.asList(defaultOptions.staticOptions());
        this.elementType = defaultOptions.elementType().getClass();
        this.listType = defaultOptions.type();
        this.optionsSource = defaultOptions.source();
        if (defaultOptions.source() == ListOptions.OptionsSource.STATIC) {
            this.staticOptions = Arrays.asList(defaultOptions.staticOptions());
        }

        optionsCache.put(id(), defaultOptions);
        return defaultOptions;
    }

    public void invalidateSettingsCache() {
        optionsCache.remove(id());
    }
}
