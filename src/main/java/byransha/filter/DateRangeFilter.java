package byransha.filter;

import byransha.BBGraph;
import byransha.BNode;
import byransha.BooleanNode;
import byransha.DateNode;
import byransha.ValuedNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class DateRangeFilter extends FilterNode {

    public DateNode fromDate;
    public DateNode toDate;
    public BooleanNode includeNull;

    private static final DateTimeFormatter[] SUPPORTED_FORMATS = {
        DateTimeFormatter.ISO_LOCAL_DATE,
        DateTimeFormatter.ofPattern("MM/dd/yyyy"),
        DateTimeFormatter.ofPattern("dd/MM/yyyy"),
        DateTimeFormatter.ofPattern("yyyy/MM/dd"),
        DateTimeFormatter.ofPattern("dd-MM-yyyy"),
        DateTimeFormatter.ofPattern("MM-dd-yyyy"),
    };

    public DateRangeFilter(BBGraph g) {
        super(g);
        fromDate = BNode.create(g, DateNode.class);
        toDate = BNode.create(g, DateNode.class);
        includeNull = BNode.create(g, BooleanNode.class);
        includeNull.set(true);
    }

    public DateRangeFilter(BBGraph g, int id) {
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

        LocalDate nodeDate = parseDate(fieldValue);
        if (nodeDate == null) {
            return includeNull.get();
        }

        LocalDate from = parseDate(fromDate.get());
        LocalDate to = parseDate(toDate.get());

        if (from == null && to == null) {
            return true;
        }

        boolean afterFrom = from == null || !nodeDate.isBefore(from);
        boolean beforeTo = to == null || !nodeDate.isAfter(to);

        return afterFrom && beforeTo;
    }

    @Override
    public List<Class<? extends BNode>> getSupportedTypes() {
        return List.of(DateNode.class);
    }

    @Override
    public boolean supportsNodeType(Class<? extends BNode> nodeClass) {
        List<Class<? extends BNode>> supportedTypes = getSupportedTypes();
        return supportedTypes
            .stream()
            .anyMatch(supportedType ->
                supportedType.isAssignableFrom(nodeClass)
            );
    }

    @Override
    public void configure(ObjectNode config) {
        super.configure(config);

        if (config.has("fromDate")) {
            fromDate.set(config.get("fromDate").asText());
        }
        if (config.has("toDate")) {
            toDate.set(config.get("toDate").asText());
        }
        if (config.has("includeNull")) {
            includeNull.set(config.get("includeNull").asBoolean());
        }
    }

    private LocalDate parseDate(Object dateValue) {
        if (dateValue == null) {
            return null;
        }

        String dateString;
        if (dateValue instanceof DateNode dateNode) {
            dateString = dateNode.get();
        } else if (dateValue instanceof ValuedNode<?> valuedNode) {
            dateString = valuedNode.getAsString();
        } else {
            dateString = dateValue.toString();
        }

        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }

        dateString = dateString.trim();

        for (DateTimeFormatter formatter : SUPPORTED_FORMATS) {
            try {
                return LocalDate.parse(dateString, formatter);
            } catch (DateTimeParseException e) {}
        }

        System.err.println("Could not parse date: " + dateString);
        return null;
    }

    @Override
    public String getFilterDescription() {
        String from = fromDate.get();
        String to = toDate.get();

        if (from == null && to == null) {
            return "Date range filter (no range set)";
        } else if (from != null && to != null) {
            return String.format("Date range: %s to %s", from, to);
        } else if (from != null) {
            return String.format("Date from: %s", from);
        } else {
            return String.format("Date until: %s", to);
        }
    }

    @Override
    public String prettyName() {
        String from = fromDate.get();
        String to = toDate.get();

        if (from == null && to == null) {
            return "Date Range Filter";
        } else if (from != null && to != null) {
            return String.format("Dates: %s - %s", from, to);
        } else if (from != null) {
            return "Dates from: " + from;
        } else {
            return "Dates until: " + to;
        }
    }
}
