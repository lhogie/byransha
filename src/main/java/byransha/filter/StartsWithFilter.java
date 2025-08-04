package byransha.filter;

import byransha.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;

public class StartsWithFilter extends FilterNode {

    public StringNode prefix;
    public BooleanNode caseSensitive;

    public StartsWithFilter(BBGraph g) {
        super(g);
        prefix = g.create( StringNode.class);
        caseSensitive = g.create( BooleanNode.class);
        caseSensitive.set("caseSensitive", this, false);
    }

    public StartsWithFilter(BBGraph g, int id) {
        super(g, id);
    }

    @Override
    protected void initialized() {
        super.initialized();
        if (prefix.get() == null) {
            prefix.set("");
        }
    }

    @Override
    public boolean filter(BNode node) {
        String prefixValue = prefix.get();
        if (prefixValue == null || prefixValue.isEmpty()) {
            return true;
        }

        Object fieldValue = getFieldValue(node);
        String stringValue = valueToString(fieldValue);

        if (stringValue == null) {
            return false;
        }

        if (caseSensitive.get()) {
            return stringValue.startsWith(prefixValue);
        } else {
            return stringValue
                .toLowerCase()
                .startsWith(prefixValue.toLowerCase());
        }
    }

    @Override
    public List<Class<? extends BNode>> getSupportedTypes() {
        return List.of();
    }

    @Override
    public void configure(ObjectNode config) {
        super.configure(config);

        if (config.has("prefix")) {
            prefix.set(config.get("prefix").asText());
        }
        if (config.has("caseSensitive")) {
            caseSensitive.set(config.get("caseSensitive").asBoolean());
        }
    }

    @Override
    public String getFilterDescription() {
        String prefixValue = prefix.get();
        if (prefixValue == null || prefixValue.isEmpty()) {
            return "Starts with filter (no prefix set)";
        }

        String sensitivity = caseSensitive.get()
            ? "case-sensitive"
            : "case-insensitive";
        return String.format("Starts with '%s' (%s)", prefixValue, sensitivity);
    }

    @Override
    public String prettyName() {
        String prefixValue = prefix.get();
        if (prefixValue == null || prefixValue.isEmpty()) {
            return "Starts With Filter";
        }
        return "Starts with: " + prefixValue;
    }
}
