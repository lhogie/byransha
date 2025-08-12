package byransha;

import byransha.annotations.ListOptions;
import toools.io.ser.Serializer;

import java.io.File;
import java.lang.annotation.ElementType;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ListNode<T> extends ValuedNode<List<T>> {

    private Class<?> elementType;
    private Predicate<String> optionsFilter;

    private List<String> staticOptions = new ArrayList<>();
    private ListOptions.ListType listType;
    private ListOptions.OptionsSource optionsSource;
    private static final Map<Integer, ListOptions> optionsCache =
        new ConcurrentHashMap<>();
    private static final Random RANDOM = new Random();

    public ListNode(BBGraph db) {
        super(db);
    }

    public ListNode(BBGraph g, int id) {
        super(g, id);
    }

    @Override
    protected void saveValue(ValueHistoryEntry<List<T>> e, Consumer<File> writingFiles) {

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
            case MULTIDROPDOWN -> "a multi-select dropdown";
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
                consumer.accept(i++ + ". " + bNode.id(), bNode);
            }
        }
    }

    public void add(T element, User creator) {
        if (element == null) return;

        ListOptions options = getListOptions();

        if (options.maxItems() > 0 && size() >= options.maxItems()) {
            throw new IllegalStateException(
                "Maximum number of items reached: " + options.maxItems()
            );
        }

        List<T> newL = new ArrayList<>(get());
        newL.add(element);
        set(newL, creator);
        invalidateOutsCache();
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
        }
    }

    public void select(String option) {
        if (option == null || option.isEmpty()) {
            throw new IllegalArgumentException(
                "Option cannot be null or empty"
            );
        }

        List<String> filteredOptions = getFilteredStaticOptions();
        if (!filteredOptions.contains(option)) {
            throw new IllegalArgumentException(
                "Option not found in static options: " + option
            );
        }

        var existingElement = elements.stream().findFirst();

        if (existingElement.isPresent()) {
            StringNode existingNode = (StringNode) existingElement.get();
            existingNode.set(option);
        } else {
            var n = graph.create(StringNode.class);
            n.set(option);
            add((T) n);
        }
    }

    public void select(int index) {
        List<String> filteredOptions = getFilteredStaticOptions();
        if (index < 0 || index >= filteredOptions.size()) {
            throw new IndexOutOfBoundsException(
                "Index out of bounds: " + index
            );
        }

        var option = filteredOptions.get(index);
        var existingElement = elements.stream().findFirst();

        if (existingElement.isPresent()) {
            StringNode existingNode = (StringNode) existingElement.get();
            existingNode.set(option);
        } else {
            var n = graph.create(StringNode.class);
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

        return getFilteredStaticOptions().indexOf(selected);
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

    public ListOptions.ElementType getElementType() {
        return getListOptions().elementType();
    }

    public void removeAll() {
        elements.clear();
        invalidateOutsCache();
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
        return getFilteredStaticOptions();
    }

    private List<String> getFilteredStaticOptions() {
        if (optionsFilter == null) {
            return List.copyOf(staticOptions);
        }
        return staticOptions.stream().filter(optionsFilter).toList();
    }

    public List<String> getAllStaticOptions() {
        return List.copyOf(staticOptions);
    }

    public void setStaticOptions(List<String> options) {
        if (
            getListOptions().source() == ListOptions.OptionsSource.PROGRAMMATIC
        ) {
            this.staticOptions = new ArrayList<>(options);
        }
    }

    public void addStaticOption(String option) {
        if (
            getListOptions().source() == ListOptions.OptionsSource.PROGRAMMATIC
        ) {
            if (!staticOptions.contains(option)) {
                staticOptions.add(option);
            }
        }
    }

    public List<String> getOptionsList() {
        return getFilteredStaticOptions();
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

    public void setOptionsFilter(Predicate<String> filter) {
        this.optionsFilter = filter;
    }

    public Predicate<String> getOptionsFilter() {
        return optionsFilter;
    }

    public void clearOptionsFilter() {
        this.optionsFilter = null;
    }

    private ListOptions getListOptions() {
        if (this != null && optionsCache.containsKey(id())) {
            return optionsCache.get(id());
        }

        for (InLink inLink : ins()) {
            BNode sourceNode = inLink.source();

            // Check fields in the entire class hierarchy
            Class<?> currentClass = sourceNode.getClass();
            while (currentClass != null && currentClass != Object.class) {
                for (Field field : currentClass.getDeclaredFields()) {
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
                                    this.staticOptions = Arrays.asList(
                                        annotation.staticOptions()
                                    );
                                    this.listType = annotation.type();
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
                            }
                        } catch (IllegalAccessException e) {}
                    }
                }
                // Move to the parent class
                currentClass = currentClass.getSuperclass();
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
