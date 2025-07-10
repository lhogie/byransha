package byransha;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;

public class ChoiceNode<N extends BNode> extends PersistingNode {

    public enum SelectionMode {
        SINGLE,
        MULTIPLE
    }

    public enum DisplayMode {
        DROPDOWN,
        CHECKBOX,
        RADIO,
        LIST
    }

    private final List<N> options = new CopyOnWriteArrayList<>();
    private final List<N> selected = new CopyOnWriteArrayList<>();

    private SelectionMode selectionMode = SelectionMode.SINGLE;
    private DisplayMode displayMode = DisplayMode.LIST;
    private boolean allowNewEntries = false;

    public ChoiceNode(BBGraph db) {
        super(db);
    }

    public ChoiceNode(BBGraph db, int id) {
        super(db, id);
    }

    // Configuration methods
    public ChoiceNode<N> setSelectionMode(SelectionMode mode) {
        this.selectionMode = mode;
        return this;
    }

    public ChoiceNode<N> setDisplayMode(DisplayMode mode) {
        this.displayMode = mode;
        return this;
    }

    public ChoiceNode<N> setAllowNewEntries(boolean allowed) {
        this.allowNewEntries = allowed;
        return this;
    }

    public ChoiceNode<N> addOption(N option) {
        this.options.add(option);
        return this;
    }

    public ChoiceNode<N> addOptions(List<N> options) {
        this.options.addAll(options);
        return this;
    }
    
    public ChoiceNode<N> addOptions(N... options) {
        for (N option : options) {
            this.options.add(option);
        }
        return this;
    }

    // Data manipulation methods
    public void select(N item) {
        if (!options.contains(item)) {
            if (allowNewEntries) {
                options.add(item);
            } else {
                throw new IllegalArgumentException("Item is not a valid option.");
            }
        }

        if (selectionMode == SelectionMode.SINGLE) {
            selected.clear();
            selected.add(item);
        } else {
            if (!selected.contains(item)) {
                selected.add(item);
            }
        }
        this.save(f -> {});
    }

    public void deselect(N item) {
        selected.remove(item);
        this.save(f -> {});
    }

    // Accessors
    public List<N> getOptions() {
        return new ArrayList<>(options);
    }

    public List<N> getSelected() {
        return new ArrayList<>(selected);
    }

    public N getFirstSelected() {
        return selected.isEmpty() ? null : selected.get(0);
    }

    public boolean isSelected(N item) {
        return selected.contains(item);
    }
    
    public SelectionMode getSelectionMode() {
        return selectionMode;
    }

    public DisplayMode getDisplayMode() {
        return displayMode;
    }

    public boolean areNewEntriesAllowed() {
        return allowNewEntries;
    }

    @Override
    public String whatIsThis() {
        return "A choice node with " + options.size() + " options.";
    }

    @Override
    public String prettyName() {
        return "a choice";
    }

    @Override
    public void forEachOut(BiConsumer<String, BNode> consumer) {
        ListNode<N> optionsList = new ListNode<>(graph);
        options.forEach(optionsList::add);
        consumer.accept("Options", optionsList);

        ListNode<N> selectedList = new ListNode<>(graph);
        selected.forEach(selectedList::add);
        consumer.accept("Selected", selectedList);
    }
}
