package byransha.export;

import byransha.*;
import byransha.labmodel.model.v0.BusinessNode;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Modifier;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;
import java.lang.reflect.Field;

/**
 * A utility class for exporting BusinessNode data to CSV format.
 * Allows customization of which recursive nodes to include in the export.
 */
public class CSVExporter {

    /**
     * Export a BusinessNode to a CSV file.
     *
     * @param node The BusinessNode to export
     * @param file The file to write the CSV data to
     * @param includeFields A predicate to determine which fields to include in the export
     * @param includeRecursiveNodes A predicate to determine which recursive nodes to include in the export
     * @param maxDepth The maximum depth to traverse recursive nodes
     * @throws IOException If an I/O error occurs
     */
    public static void exportToCSV(BusinessNode node, File file, 
                                  Predicate<Field> includeFields,
                                  Predicate<BNode> includeRecursiveNodes,
                                  int maxDepth) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            exportToCSV(node, writer, includeFields, includeRecursiveNodes, maxDepth);
        }
    }

    /**
     * Export a BusinessNode to a CSV file.
     *
     * @param node The BusinessNode to export
     * @param writer The writer to write the CSV data to
     * @param includeFields A predicate to determine which fields to include in the export
     * @param includeRecursiveNodes A predicate to determine which recursive nodes to include in the export
     * @param maxDepth The maximum depth to traverse recursive nodes
     * @throws IOException If an I/O error occurs
     */
    public static void exportToCSV(BusinessNode node, Writer writer,
                                  Predicate<Field> includeFields,
                                  Predicate<BNode> includeRecursiveNodes,
                                  int maxDepth) throws IOException {
        // Collect all fields to export
        Map<String, Object> fieldsToExport = new LinkedHashMap<>();
        collectFields(node, "", fieldsToExport, includeFields, includeRecursiveNodes, maxDepth, 0);

        // Write header row
        writer.write(String.join(";", fieldsToExport.keySet()) + "\n");

        // Write data row
        List<String> values = new ArrayList<>();
        for (Object value : fieldsToExport.values()) {
            values.add(formatValue(value));
        }
        writer.write(String.join(";", values) + "\n");
    }

    /**
     * Export multiple BusinessNodes to a CSV file.
     *
     * @param nodes The BusinessNodes to export
     * @param file The file to write the CSV data to
     * @param includeFields A predicate to determine which fields to include in the export
     * @param includeRecursiveNodes A predicate to determine which recursive nodes to include in the export
     * @param maxDepth The maximum depth to traverse recursive nodes
     * @throws IOException If an I/O error occurs
     */
    public static void exportToCSV(List<BusinessNode> nodes, File file,
                                  Predicate<Field> includeFields,
                                  Predicate<BNode> includeRecursiveNodes,
                                  int maxDepth) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            exportToCSV(nodes, writer, includeFields, includeRecursiveNodes, maxDepth);
        }
    }

    /**
     * Export multiple BusinessNodes to a CSV file.
     *
     * @param nodes The BusinessNodes to export
     * @param writer The writer to write the CSV data to
     * @param includeFields A predicate to determine which fields to include in the export
     * @param includeRecursiveNodes A predicate to determine which recursive nodes to include in the export
     * @param maxDepth The maximum depth to traverse recursive nodes
     * @throws IOException If an I/O error occurs
     */
    public static void exportToCSV(List<BusinessNode> nodes, Writer writer,
                                  Predicate<Field> includeFields,
                                  Predicate<BNode> includeRecursiveNodes,
                                  int maxDepth) throws IOException {
        if (nodes == null || nodes.isEmpty()) {
            return;
        }

        // Collect all fields from all nodes to ensure consistent columns
        Set<String> allFields = new LinkedHashSet<>();
        List<Map<String, Object>> allNodeFields = new ArrayList<>();

        for (BusinessNode node : nodes) {
            Map<String, Object> fieldsToExport = new LinkedHashMap<>();
            collectFields(node, "", fieldsToExport, includeFields, includeRecursiveNodes, maxDepth, 0);
            allNodeFields.add(fieldsToExport);
            allFields.addAll(fieldsToExport.keySet());
        }

        // Write header row
        writer.write(String.join(";", allFields) + "\n");

        // Write data rows
        for (Map<String, Object> nodeFields : allNodeFields) {
            List<String> values = new ArrayList<>();
            for (String field : allFields) {
                Object value = nodeFields.getOrDefault(field, "");
                values.add(formatValue(value));
            }
            writer.write(String.join(";", values) + "\n");
        }
    }

    /**
     * Recursively collect fields from a node and its recursive nodes.
     *
     * @param node The node to collect fields from
     * @param prefix The prefix to add to field names
     * @param fieldsToExport The map to store collected fields
     * @param includeFields A predicate to determine which fields to include
     * @param includeRecursiveNodes A predicate to determine which recursive nodes to include
     * @param maxDepth The maximum depth to traverse recursive nodes
     * @param currentDepth The current depth in the traversal
     */
    private static void collectFields(Object node, String prefix, Map<String, Object> fieldsToExport,
                                     Predicate<Field> includeFields,
                                     Predicate<BNode> includeRecursiveNodes,
                                     int maxDepth, int currentDepth) {
        if (node == null || currentDepth > maxDepth) {
            return;
        }

        Class<?> clazz = node.getClass();
        while (clazz != null && clazz != BNode.class) {
            // Add basic fields
            for (Field field : clazz.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }

                field.setAccessible(true);

                if (includeFields.test(field)) {
                    try {
                        Object value = field.get(node);
                        String fieldName = prefix + (prefix.isEmpty() ? "" : ".") + field.getName();

                        switch (value) {
                            case DropdownNode<?> dropdownNode ->
                                    handleDropDown(dropdownNode, fieldName, fieldsToExport, includeFields, includeRecursiveNodes, maxDepth, currentDepth);
                            case DateNode dateNode ->
                                    handleDateNode(dateNode, fieldName, fieldsToExport, includeFields, includeRecursiveNodes, maxDepth, currentDepth);
                            case ValuedNode<?> valuedNode ->
                                    fieldsToExport.put(fieldName, valuedNode.get());
                            case SetNode<?> setNode ->
                                    handleSetNode(setNode, fieldName, fieldsToExport, includeFields, includeRecursiveNodes, maxDepth, currentDepth);
                            case ListNode<?> listNode ->
                                    handleListNode(listNode, fieldName, fieldsToExport, includeFields, includeRecursiveNodes, maxDepth, currentDepth);
                            case BNode bNode when currentDepth < maxDepth -> {
                                if (includeRecursiveNodes.test(bNode)) {
                                    collectFields(value, fieldName, fieldsToExport, includeFields, includeRecursiveNodes, maxDepth, currentDepth + 1);
                                }
                            }
                            case null, default ->
                                    fieldsToExport.put(fieldName, value);
                        }
                    } catch (IllegalAccessException e) {
                        // Skip fields that can't be accessed
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }
    }

    /**
     * Handle SetNode fields by collecting their elements.
     *
     * @param setNode The SetNode to handle
     * @param fieldName The name of the field
     * @param fieldsToExport The map to store collected fields
     * @param includeFields A predicate to determine which fields to include
     * @param includeRecursiveNodes A predicate to determine which recursive nodes to include
     * @param maxDepth The maximum depth to traverse recursive nodes
     * @param currentDepth The current depth in the traversal
     */
    private static void handleSetNode(SetNode<?> setNode, String fieldName, Map<String, Object> fieldsToExport,
                                     Predicate<Field> includeFields,
                                     Predicate<BNode> includeRecursiveNodes,
                                     int maxDepth, int currentDepth) {
        List<String> elements = new ArrayList<>();

        setNode.forEachOut((name, node) -> {
            if (node instanceof ValuedNode) {
                // For ValuedNode, get the actual value
                ValuedNode<?> valuedNode = (ValuedNode<?>) node;
                elements.add(formatValue(valuedNode.get()));
            } else if (includeRecursiveNodes.test(node) && currentDepth < maxDepth) {
                // For other nodes, add their string representation
                elements.add(node.prettyName());
            }
        });

        fieldsToExport.put(fieldName, String.join(",", elements));
    }

    /**
     * Handle ListNode fields by collecting their elements.
     *
     * @param listNode The ListNode to handle
     * @param fieldName The name of the field
     * @param fieldsToExport The map to store collected fields
     * @param includeFields A predicate to determine which fields to include
     * @param includeRecursiveNodes A predicate to determine which recursive nodes to include
     * @param maxDepth The maximum depth to traverse recursive nodes
     * @param currentDepth The current depth in the traversal
     */
    private static void handleListNode(ListNode<?> listNode, String fieldName, Map<String, Object> fieldsToExport,
                                     Predicate<Field> includeFields,
                                     Predicate<BNode> includeRecursiveNodes,
                                     int maxDepth, int currentDepth) {
        List<String> elements = new ArrayList<>();

        listNode.forEachOut((name, node) -> {
            if (node instanceof ValuedNode) {
                // For ValuedNode, get the actual value
                ValuedNode<?> valuedNode = (ValuedNode<?>) node;
                elements.add(formatValue(valuedNode.get()));
            } else if (includeRecursiveNodes.test(node) && currentDepth < maxDepth) {
                // For other nodes, add their string representation
                elements.add(node.prettyName());
            }
        });

        fieldsToExport.put(fieldName, String.join(",", elements));
    }

    private static void handleDropDown(DropdownNode<?> dropdownNode, String fieldName, Map<String, Object> fieldsToExport,
                                     Predicate<Field> includeFields,
                                     Predicate<BNode> includeRecursiveNodes,
                                     int maxDepth, int currentDepth) {
        if (dropdownNode.get() != null) {
            fieldsToExport.put(fieldName, dropdownNode.get().prettyName());
        } else {
            fieldsToExport.put(fieldName, null);
        }
    }

    private static void handleDateNode(DateNode dateNode, String fieldName, Map<String, Object> fieldsToExport,
                                       Predicate<Field> includeFields,
                                       Predicate<BNode> includeRecursiveNodes,
                                       int maxDepth, int currentDepth) {
        if (dateNode.get() != null) {
            // Use new date to convert the date in a readable format (it's save with T...)
            OffsetDateTime odt = OffsetDateTime.parse(dateNode.get());
            Date parsedDate = Date.from(odt.toInstant());

            DateFormat excelDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String formattedString = excelDateFormat.format(parsedDate);
            fieldsToExport.put(fieldName, formattedString);
        } else {
            fieldsToExport.put(fieldName, null);
        }
    }

    /**
     * Format a value for CSV output, escaping commas and quotes.
     *
     * @param value The value to format
     * @return The formatted value
     */
    private static String formatValue(Object value) {
        if (value == null) {
            return "";
        }

        String stringValue = value.toString();

        // Escape quotes and wrap in quotes if contains comma or quote
        if (stringValue.contains(",") || stringValue.contains("\"") || stringValue.contains("\n")) {
            stringValue = stringValue.replace("\"", "\"\"");
            stringValue = "\"" + stringValue + "\"";
        }

        return stringValue;
    }

    /**
     * Create a predicate that includes all fields.
     *
     * @return A predicate that includes all fields
     */
    public static Predicate<Field> includeAllFields() {
        return field -> true;
    }

    /**
     * Create a predicate that excludes fields with specific names.
     *
     * @param excludedFieldNames The names of fields to exclude
     * @return A predicate that excludes the specified fields
     */
    public static Predicate<Field> excludeFields(String... excludedFieldNames) {
        Set<String> excludedFields = new HashSet<>(Arrays.asList(excludedFieldNames));
        return field -> !excludedFields.contains(field.getName());
    }

    /**
     * Create a predicate that includes only fields with specific names.
     *
     * @param includedFieldNames The names of fields to include
     * @return A predicate that includes only the specified fields
     */
    public static Predicate<Field> includeOnlyFields(String... includedFieldNames) {
        Set<String> includedFields = new HashSet<>(Arrays.asList(includedFieldNames));
        return field -> includedFields.contains(field.getName());
    }

    /**
     * Create a predicate that includes all nodes.
     *
     * @return A predicate that includes all nodes
     */
    public static Predicate<BNode> includeAllNodes() {
        return node -> true;
    }

    /**
     * Create a predicate that includes only nodes of specific types.
     *
     * @param nodeTypes The types of nodes to include
     * @return A predicate that includes only nodes of the specified types
     */
    @SafeVarargs
    public static Predicate<BNode> includeOnlyNodeTypes(Class<? extends BNode>... nodeTypes) {
        Set<Class<? extends BNode>> includedTypes = new HashSet<>(Arrays.asList(nodeTypes));
        return node -> {
            for (Class<? extends BNode> type : includedTypes) {
                if (type.isInstance(node)) {
                    return true;
                }
            }
            return false;
        };
    }
}
