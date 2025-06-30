package byransha.web.endpoint;

import byransha.*;
import byransha.export.CSVExporter;
import byransha.labmodel.model.v0.BusinessNode;
import byransha.web.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpsExchange;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.lang.reflect.Field;

/**
 * Endpoint for exporting BusinessNode data to CSV format.
 * This endpoint allows customization of which fields and recursive nodes to include in the export.
 */
public class ExportCSV extends NodeEndpoint<BNode> {

    public ExportCSV(BBGraph db) {
        super(db);
    }

    public ExportCSV(BBGraph db, int id) {
        super(db, id);
    }

    @Override
    public String whatItDoes() {
        return "Exports BusinessNode data to CSV format with customizable field and node inclusion.";
    }

    @Override
    public EndpointResponse exec(ObjectNode in, User user, WebServer webServer, HttpsExchange exchange, BNode node) throws Throwable {
        // Check if the node is a BusinessNode
        if (!(node instanceof BusinessNode)) {
            return ErrorResponse.badRequest("Node must be a BusinessNode for CSV export.");
        }

        // Get export parameters from the request
        boolean includeAllFields = in.has("includeAllFields") && in.get("includeAllFields").asBoolean(true);
        boolean includeAllNodes = in.has("includeAllNodes") && in.get("includeAllNodes").asBoolean(true);
        int maxDepth = in.has("maxDepth") ? in.get("maxDepth").asInt(2) : 2;

        // Get excluded fields if specified
        String[] excludedFields = in.has("excludedFields") ? 
            in.get("excludedFields").asText().split(",") : new String[0];

        // Get included fields if specified
        String[] includedFields = in.has("includedFields") ? 
            in.get("includedFields").asText().split(",") : new String[0];

        // Get included node types if specified
        String[] includedNodeTypes = in.has("includedNodeTypes") ? 
            in.get("includedNodeTypes").asText().split(",") : new String[0];

        // Check if we're exporting multiple nodes
        boolean exportMultiple = in.has("exportMultiple") && in.get("exportMultiple").asBoolean(false);

        // Create field predicate
        Predicate<Field> fieldPredicate;
        if (includeAllFields) {
            fieldPredicate = CSVExporter.includeAllFields();
        } else if (includedFields.length > 0) {
            fieldPredicate = CSVExporter.includeOnlyFields(includedFields);
        } else {
            fieldPredicate = CSVExporter.excludeFields(excludedFields);
        }

        // Create node predicate
        Predicate<BNode> nodePredicate;
        if (includeAllNodes) {
            nodePredicate = CSVExporter.includeAllNodes();
        } else if (includedNodeTypes.length > 0) {
            // Convert string class names to actual classes
            List<Class<? extends BNode>> nodeClasses = new ArrayList<>();
            for (String className : includedNodeTypes) {
                try {
                    @SuppressWarnings("unchecked")
                    Class<? extends BNode> nodeClass = (Class<? extends BNode>) Class.forName(className);
                    nodeClasses.add(nodeClass);
                } catch (ClassNotFoundException | ClassCastException e) {
                    // Skip invalid class names
                }
            }

            if (nodeClasses.isEmpty()) {
                nodePredicate = CSVExporter.includeAllNodes();
            } else {
                nodePredicate = CSVExporter.includeOnlyNodeTypes(
                    nodeClasses.toArray(new Class[0])
                );
            }
        } else {
            nodePredicate = CSVExporter.includeAllNodes();
        }

        // Export to CSV
        StringWriter writer = new StringWriter();

        try {
            if (exportMultiple) {
                // Get the list of nodes to export
                List<BusinessNode> nodesToExport = new ArrayList<>();

                // If the current node is a ListNode or SetNode containing BusinessNodes
                if (node instanceof ListNode) {
                    ListNode<?> listNode = (ListNode<?>) node;
                    listNode.forEachOut((name, n) -> {
                        if (n instanceof BusinessNode) {
                            nodesToExport.add((BusinessNode) n);
                        }
                    });
                } else if (node instanceof SetNode) {
                    SetNode<?> setNode = (SetNode<?>) node;
                    setNode.forEachOut((name, n) -> {
                        if (n instanceof BusinessNode) {
                            nodesToExport.add((BusinessNode) n);
                        }
                    });
                } else {
                    // Just export the current node
                    nodesToExport.add((BusinessNode) node);
                }

                if (nodesToExport.isEmpty()) {
                    return ErrorResponse.badRequest("No BusinessNodes found to export.");
                }

                CSVExporter.exportToCSV(nodesToExport, writer, fieldPredicate, nodePredicate, maxDepth);
            } else {
                // Export single node
                CSVExporter.exportToCSV((BusinessNode) node, writer, fieldPredicate, nodePredicate, maxDepth);
            }

            // Return the CSV data
            String csvContent = writer.toString();
            if (csvContent.isEmpty()) {
                return ErrorResponse.serverError("CSV export produced empty content.");
            }
            return new EndpointTextResponse("text/csv", pw -> pw.write(csvContent));
        } catch (Exception e) {
            return ErrorResponse.serverError("Error during CSV export: " + e.getMessage());
        }
    }
}
