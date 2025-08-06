package byransha.filter;

import byransha.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;

public class ContainsFilter extends FieldFilterNode {

    public StringNode searchText;
    public BooleanNode caseSensitive;
    public BooleanNode wholeWordsOnly;

    public ContainsFilter(BBGraph g) {
        super(g);
        searchText = g.create(StringNode.class);
        caseSensitive = g.create(BooleanNode.class);
        wholeWordsOnly = g.create(BooleanNode.class);

        caseSensitive.set("caseSensitive", this, false);
        wholeWordsOnly.set("wholeWordsOnly", this, false);
    }

    public ContainsFilter(BBGraph g, int id) {
        super(g, id);
    }

    @Override
    protected void initialized() {
        super.initialized();
        if (searchText.get() == null) {
            searchText.set("");
        }
    }

    @Override
    public boolean filter(BNode node) {
        String searchValue = searchText.get();
        if (searchValue == null || searchValue.isEmpty()) {
            return true;
        }

        Object fieldValue = getFieldValue(node);
        String stringValue = valueToString(fieldValue);

        if (stringValue == null) {
            return false;
        }

        String haystack = caseSensitive.get()
            ? stringValue
            : stringValue.toLowerCase();
        String needle = caseSensitive.get()
            ? searchValue
            : searchValue.toLowerCase();

        if (wholeWordsOnly.get()) {
            return containsWholeWord(haystack, needle);
        } else {
            return haystack.contains(needle);
        }
    }

    private boolean containsWholeWord(String text, String searchTerm) {
        if (text == null || searchTerm == null || searchTerm.isEmpty()) {
            return false;
        }

        String pattern =
            "\\b" + java.util.regex.Pattern.quote(searchTerm) + "\\b";
        return text.matches(".*" + pattern + ".*");
    }

    @Override
    public List<Class<? extends BNode>> getSupportedTypes() {
        return List.of();
    }

    @Override
    public void configure(ObjectNode config) {
        super.configure(config);

        if (config.has("searchText")) {
            searchText.set(config.get("searchText").asText());
        }
        if (config.has("caseSensitive")) {
            caseSensitive.set(config.get("caseSensitive").asBoolean());
        }
        if (config.has("wholeWordsOnly")) {
            wholeWordsOnly.set(config.get("wholeWordsOnly").asBoolean());
        }
    }

    @Override
    public String getFilterDescription() {
        String searchValue = searchText.get();
        if (searchValue == null || searchValue.isEmpty()) {
            return "Contains filter (no search text set)";
        }

        String sensitivity = caseSensitive.get()
            ? "case-sensitive"
            : "case-insensitive";
        String wordMode = wholeWordsOnly.get()
            ? "whole words only"
            : "substring";

        return String.format(
            "Contains '%s' (%s, %s)",
            searchValue,
            sensitivity,
            wordMode
        );
    }

    @Override
    public String prettyName() {
        String searchValue = searchText.get();
        if (searchValue == null || searchValue.isEmpty()) {
            return "Contains Filter";
        }

        String prefix = wholeWordsOnly.get() ? "Word: " : "Contains: ";
        return prefix + searchValue;
    }
}
