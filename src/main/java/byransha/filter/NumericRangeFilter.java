package byransha.filter;

import byransha.*;
import byransha.labmodel.model.v0.NodeBuilder;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;

public class NumericRangeFilter extends FieldFilterNode {
    public StringNode minValue;
    public StringNode maxValue;
    public BooleanNode includeNull;
    public BooleanNode includeMin;
    public BooleanNode includeMax;

    public NumericRangeFilter(BBGraph g, User creator, InstantiationInfo ii) {
        super(g, creator, ii);
        endOfConstructor();
    }

    @Override
    protected void createOuts(User creator) {
        minValue = new StringNode(g, creator, InstantiationInfo.persisting);
        maxValue = new StringNode(g, creator, InstantiationInfo.persisting);
        includeNull = new BooleanNode(g, creator, InstantiationInfo.persisting);
        includeMin = new BooleanNode(g, creator, InstantiationInfo.persisting);
        includeMax = new BooleanNode(g, creator, InstantiationInfo.persisting);

        includeNull.set("includeNull", this, true, creator);
        includeMin.set("includeMin", this, true, creator);
        includeMax.set("includeMax", this, true, creator);
    }

    @Override
    public boolean filter(BNode node) {
        Object fieldValue = getFieldValue(node);

        if (fieldValue == null) {
            return includeNull.get();
        }

        Double nodeValue = parseNumeric(fieldValue);
        if (nodeValue == null) {
            return includeNull.get();
        }

        Double min = parseNumeric(minValue.get());
        Double max = parseNumeric(maxValue.get());

        if (min == null && max == null) {
            return true;
        }

        boolean aboveMin = true;
        if (min != null) {
            if (includeMin.get()) {
                aboveMin = nodeValue >= min;
            } else {
                aboveMin = nodeValue > min;
            }
        }

        boolean belowMax = true;
        if (max != null) {
            if (includeMax.get()) {
                belowMax = nodeValue <= max;
            } else {
                belowMax = nodeValue < max;
            }
        }

        return aboveMin && belowMax;
    }

    @Override
    public List<Class<? extends BNode>> getSupportedTypes() {
        return List.of(IntNode.class);
    }

    @Override
    public boolean supportsNodeType(Class<? extends BNode> nodeClass) {
        List<Class<? extends BNode>> supportedTypes = getSupportedTypes();
        boolean isDirectlySupported = supportedTypes
            .stream()
            .anyMatch(supportedType ->
                supportedType.isAssignableFrom(nodeClass)
            );

        return isDirectlySupported || true;
    }

    @Override
    public void configure(ObjectNode config, User user) {
        super.configure(config, user);

        if (config.has("minValue")) {
            minValue.set(config.get("minValue").asText(), user);
        }
        if (config.has("maxValue")) {
            maxValue.set(config.get("maxValue").asText(), user);
        }
        if (config.has("includeNull")) {
            includeNull.set(config.get("includeNull").asBoolean(), user);
        }
        if (config.has("includeMin")) {
            includeMin.set(config.get("includeMin").asBoolean(), user);
        }
        if (config.has("includeMax")) {
            includeMax.set(config.get("includeMax").asBoolean(), user);
        }
    }

    private Double parseNumeric(Object value) {
        if (value == null) {
            return null;
        }

        String numericString;
        if (value instanceof IntNode intNode) {
            Integer intValue = intNode.get();
            return intValue != null ? intValue.doubleValue() : null;
        } else if (value instanceof ValuedNode<?> valuedNode) {
            numericString = valuedNode.getAsString();
        } else {
            numericString = value.toString();
        }

        if (numericString == null || numericString.trim().isEmpty()) {
            return null;
        }

        numericString = numericString.trim();

        try {
            return Double.parseDouble(numericString);
        } catch (NumberFormatException e) {
            String cleaned = numericString.replaceAll("[^\\d.-]", "");
            if (!cleaned.isEmpty()) {
                try {
                    return Double.parseDouble(cleaned);
                } catch (NumberFormatException ex) {}
            }
        }

        return null;
    }

    @Override
    public String getFilterDescription() {
        String min = minValue.get();
        String max = maxValue.get();

        if (min == null && max == null) {
            return "Numeric range filter (no range set)";
        }

        String minSymbol = includeMin.get() ? ">=" : ">";
        String maxSymbol = includeMax.get() ? "<=" : "<";

        if (min != null && max != null) {
            return String.format(
                "Numeric range: %s %s value %s %s",
                min,
                minSymbol,
                maxSymbol,
                max
            );
        } else if (min != null) {
            return String.format("Numeric: value %s %s", minSymbol, min);
        } else {
            return String.format("Numeric: value %s %s", maxSymbol, max);
        }
    }

    @Override
    public String prettyName() {
        String min = minValue.get();
        String max = maxValue.get();

        if (min == null && max == null) {
            return "Numeric Range Filter";
        }

        String minSymbol = includeMin.get() ? ">=" : ">";
        String maxSymbol = includeMax.get() ? "<=" : "<";

        if (min != null && max != null) {
            return String.format("Range: %s - %s", min, max);
        } else if (min != null) {
            return String.format("Min: %s%s", minSymbol, min);
        } else {
            return String.format("Max: %s%s", maxSymbol, max);
        }
    }
}
