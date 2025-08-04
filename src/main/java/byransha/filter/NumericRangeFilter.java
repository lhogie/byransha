package byransha.filter;

import byransha.BBGraph;
import byransha.BNode;
import byransha.BooleanNode;
import byransha.IntNode;
import byransha.StringNode;
import byransha.ValuedNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;

public class NumericRangeFilter extends FilterNode {

    public StringNode minValue;
    public StringNode maxValue;
    public BooleanNode includeNull;
    public BooleanNode includeMin;
    public BooleanNode includeMax;

    public NumericRangeFilter(BBGraph g) {
        super(g);
        minValue = g.create( StringNode.class);
        maxValue = g.create( StringNode.class);
        includeNull = g.create( BooleanNode.class);
        includeMin = g.create( BooleanNode.class);
        includeMax = g.create( BooleanNode.class);

        includeNull.set("includeNull", this,true);
        includeMin.set("includeMin", this, true);
        includeMax.set("includeMax", this, true);
    }

    public NumericRangeFilter(BBGraph g, int id) {
        super(g, id);
    }

    @Override
    protected void initialized() {
        super.initialized();
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
    public void configure(ObjectNode config) {
        super.configure(config);

        if (config.has("minValue")) {
            minValue.set(config.get("minValue").asText());
        }
        if (config.has("maxValue")) {
            maxValue.set(config.get("maxValue").asText());
        }
        if (config.has("includeNull")) {
            includeNull.set(config.get("includeNull").asBoolean());
        }
        if (config.has("includeMin")) {
            includeMin.set(config.get("includeMin").asBoolean());
        }
        if (config.has("includeMax")) {
            includeMax.set(config.get("includeMax").asBoolean());
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
