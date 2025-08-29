package byransha.filter;

import byransha.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;

public class StartsWithFilter extends FieldFilterNode {

    public StringNode prefix;
    public BooleanNode caseSensitive;

    public StartsWithFilter(BBGraph g, User creator, InstantiationInfo ii) {
        super(g, creator, ii);
        endOfConstructor();
    }

    @Override
    protected void createOuts(User creator) {
        super.createOuts(creator);
        prefix = new StringNode(g, creator, InstantiationInfo.persisting);
        caseSensitive = new BooleanNode(g, creator, InstantiationInfo.persisting);
        caseSensitive.set(false, creator);
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
    public void configure(ObjectNode config, User user) {
        super.configure(config, user);

        if (config.has("prefix")) {
            prefix.set(config.get("prefix").asText(), user);
        }
        if (config.has("caseSensitive")) {
            caseSensitive.set(config.get("caseSensitive").asBoolean(), user);
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
        if (prefix == null) {
            return "Starts With Filter";
        }

        String prefixValue = prefix.get();
        if (prefixValue == null || prefixValue.isEmpty()) {
            return "Starts With Filter";
        }
        return "Starts with: " + prefixValue;
    }
}
