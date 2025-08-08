package byransha.performance;

import byransha.*;
import byransha.web.Views;
import byransha.web.endpoint.*;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PerformanceTestHelper {

    private static final int SMALL_SCALE = 1000;
    private static final int MEDIUM_SCALE = 10000;
    private static final int LARGE_SCALE = 100000;
    private static final int HUGE_SCALE = 1000000;

    public static void main(String[] args) {
        System.out.println("=== Byransha Performance Test Suite ===");
        System.out.println();

        // Test different scales
        runPerformanceTests(SMALL_SCALE, "Small Scale");
        runPerformanceTests(MEDIUM_SCALE, "Medium Scale");
        runPerformanceTests(LARGE_SCALE, "Large Scale");
        runPerformanceTests(HUGE_SCALE, "Huge Scale");

        System.out.println("=== Performance Tests Complete ===");
    }

    private static void runPerformanceTests(int nodeCount, String testName) {
        System.out.println(
            "--- " + testName + " (" + nodeCount + " nodes) ---"
        );

        BBGraph graph = createTestGraph(nodeCount);
        System.out.println(
            "Created graph with " + graph.countNodes() + " nodes"
        );

        // Test 1: Incoming References Performance
        testIncomingReferencesPerformance(graph, nodeCount);

        // Test 2: ClassAttributeField Performance (Paginated)
        testClassAttributeFieldPerformance(graph, nodeCount, true);

        // Test 3: ClassAttributeField Performance (Non-Paginated)
        testClassAttributeFieldPerformance(graph, nodeCount, false);

        // Test 4: Direct Pagination Comparison
        testPaginationComparison(graph, nodeCount);

        // Test 5: Views Performance
        testViewsPerformance(graph, nodeCount);

        // Test 6: Comprehensive Endpoint Performance
        testAllEndpointsPerformance(graph, nodeCount);

        System.out.println();
    }

    /**
     * Creates a test graph with interconnected nodes
     */
    private static BBGraph createTestGraph(int nodeCount) {
        BBGraph graph = new BBGraph(null);
        List<BNode> nodes = new ArrayList<>();
        Random random = new Random(42);

        // Create nodes
        System.out.print("Creating nodes... ");
        long startTime = System.nanoTime();

        for (int i = 0; i < nodeCount; i++) {
            BNode node;
            int nodeType = i % 4;
            switch (nodeType) {
                case 0 -> node = graph.create( StringNode.class);
                case 1 -> node = graph.create( byransha.IntNode.class);
                case 2 -> node =
                    graph.create(
                    byransha.BooleanNode.class
                );
                default -> node = graph.create( ColorNode.class);
            }
            nodes.add(node);

            if (i % 1000 == 0 && i > 0) {
                System.out.print(".");
            }
        }

        long createTime = System.nanoTime() - startTime;
        System.out.println(" (" + formatTime(createTime) + ")");

        // Create connections between nodes
        System.out.print("Creating connections... ");
        startTime = System.nanoTime();

        for (int i = 0; i < nodeCount; i++) {
            BNode sourceNode = nodes.get(i);

            // Connect to 2-5 random other nodes
            int connectionCount = 2 + random.nextInt(4);
            for (int j = 0; j < connectionCount; j++) {
                int targetIndex = random.nextInt(nodeCount);
                if (targetIndex != i) {
                    BNode targetNode = nodes.get(targetIndex);
                    // This will update the incoming reference index
                    sourceNode.setField("connection_" + j, targetNode);
                }
            }

            if (i % 1000 == 0 && i > 0) {
                System.out.print(".");
            }
        }

        long connectTime = System.nanoTime() - startTime;
        System.out.println(" (" + formatTime(connectTime) + ")");

        return graph;
    }

    /**
     * Test the performance of incoming reference lookups
     * This should be O(1) with our reverse index instead of O(n²)
     */
    private static void testIncomingReferencesPerformance(
        BBGraph graph,
        int nodeCount
    ) {
        System.out.print("Testing incoming references lookup... ");

        List<BNode> testNodes = new ArrayList<>();
        graph.forEachNode(node -> {
            if (testNodes.size() < 100) {
                // Test 100 random nodes
                testNodes.add(node);
            }
        });

        long startTime = System.nanoTime();
        int totalIncomingRefs = 0;

        for (BNode node : testNodes) {
            List<BNode.InLink> incomingRefs = graph.findRefsTO(node);
            totalIncomingRefs += incomingRefs.size();
        }

        long endTime = System.nanoTime();
        long lookupTime = endTime - startTime;

        System.out.println(
            formatTime(lookupTime) +
            " (avg: " +
            formatTime(lookupTime / testNodes.size()) +
            " per lookup, " +
            totalIncomingRefs +
            " total refs)"
        );
    }

    /**
     * Test the performance of ClassAttributeField with metadata caching
     */
    private static void testClassAttributeFieldPerformance(
        BBGraph graph,
        int nodeCount,
        boolean usePagination
    ) {
        String testType = usePagination ? "paginated" : "non-paginated";
        System.out.print(
            "Testing ClassAttributeField performance (" + testType + ")... "
        );

        ClassAttributeField endpoint = new ClassAttributeField(graph);
        User testUser = graph.create( User.class);
        testUser.setAdmin(true);

        List<BNode> testNodes = new ArrayList<>();
        graph.forEachNode(node -> {
            if (testNodes.size() < 50) {
                // Test 50 nodes
                testNodes.add(node);
            }
        });

        // Test with or without optimizations based on parameter
        long startTime = System.nanoTime();
        int totalAttributes = 0;

        for (BNode node : testNodes) {
            try {
                ObjectNode input = new ObjectNode(null);
                if (usePagination) {
                    // Use pagination and skip validation for performance
                    input.set(
                        "limit",
                        new com.fasterxml.jackson.databind.node.IntNode(50)
                    );
                    input.set(
                        "skipValidation",
                        com.fasterxml.jackson.databind.node.BooleanNode.valueOf(
                            true
                        )
                    );
                }
                // Non-paginated version processes all attributes without limits
                var response = endpoint.exec(input, testUser, null, null, node);
                totalAttributes += response.toJson().size();
            } catch (Throwable e) {
                // Skip nodes that can't be processed
            }
        }

        long endTime = System.nanoTime();
        long processingTime = endTime - startTime;

        System.out.println(
            formatTime(processingTime) +
            " (avg: " +
            formatTime(processingTime / testNodes.size()) +
            " per node, " +
            totalAttributes +
            " total attributes) [" +
            testType +
            "]"
        );
    }

    /**
     * Test the performance of Views with lazy loading
     */
    private static void testViewsPerformance(BBGraph graph, int nodeCount) {
        System.out.print("Testing Views performance... ");

        Views viewsEndpoint = new Views(graph);
        User testUser = graph.create( User.class);
        testUser.setAdmin(true);

        List<BNode> testNodes = new ArrayList<>();
        graph.forEachNode(node -> {
            if (testNodes.size() < 20) {
                // Test 20 nodes
                testNodes.add(node);
            }
        });

        long startTime = System.nanoTime();
        int totalViews = 0;

        for (BNode node : testNodes) {
            try {
                ObjectNode input = new ObjectNode(null);
                // Test lazy loading - don't execute views by default
                var response = viewsEndpoint.exec(
                    input,
                    testUser,
                    null,
                    null,
                    node
                );
                totalViews += response.toJson().size();
            } catch (Throwable e) {
                // Skip nodes that can't be processed
            }
        }

        long endTime = System.nanoTime();
        long processingTime = endTime - startTime;

        System.out.println(
            formatTime(processingTime) +
            " (avg: " +
            formatTime(processingTime / testNodes.size()) +
            " per node, " +
            totalViews +
            " total views)"
        );
    }

    /**
     * Direct comparison between paginated and non-paginated ClassAttributeField performance
     */
    private static void testPaginationComparison(BBGraph graph, int nodeCount) {
        System.out.println("=== Direct Pagination Comparison ===");

        ClassAttributeField endpoint = new ClassAttributeField(graph);
        User testUser = graph.create( User.class);
        testUser.setAdmin(true);

        // Get a single test node with many attributes
        BNode[] testNodeHolder = new BNode[1];
        graph.forEachNode(node -> {
            if (
                testNodeHolder[0] == null &&
                node.outDegree() > 0 &&
                !(node instanceof User)
            ) {
                testNodeHolder[0] = node;
            }
        });
        BNode testNode = testNodeHolder[0];

        if (testNode == null) {
            System.out.println("No suitable test node found");
            return;
        }

        int iterations = Math.min(10, nodeCount / 1000); // Scale iterations with dataset size
        iterations = Math.max(1, iterations);

        System.out.println(
            "Testing with " +
            iterations +
            " iterations on node with " +
            testNode.outDegree() +
            " attributes"
        );

        // Test 1: Non-paginated (full processing)
        System.out.print("Non-paginated: ");
        long nonPaginatedTime = 0;
        int nonPaginatedAttributes = 0;

        for (int i = 0; i < iterations; i++) {
            try {
                ObjectNode input = new ObjectNode(null);
                // No pagination parameters - process everything

                long startTime = System.nanoTime();
                var response = endpoint.exec(
                    input,
                    testUser,
                    null,
                    null,
                    testNode
                );
                long endTime = System.nanoTime();

                nonPaginatedTime += (endTime - startTime);
                nonPaginatedAttributes = response.toJson().size();
            } catch (Throwable e) {
                System.out.println("Error: " + e.getMessage());
                return;
            }
        }

        // Test 2: Paginated (limited processing)
        System.out.print("Paginated: ");
        long paginatedTime = 0;
        int paginatedAttributes = 0;

        for (int i = 0; i < iterations; i++) {
            try {
                ObjectNode input = new ObjectNode(null);
                // Use pagination and skip validation
                input.set(
                    "limit",
                    new com.fasterxml.jackson.databind.node.IntNode(50)
                );
                input.set(
                    "skipValidation",
                    com.fasterxml.jackson.databind.node.BooleanNode.valueOf(
                        true
                    )
                );

                long startTime = System.nanoTime();
                var response = endpoint.exec(
                    input,
                    testUser,
                    null,
                    null,
                    testNode
                );
                long endTime = System.nanoTime();

                paginatedTime += (endTime - startTime);
                paginatedAttributes = response.toJson().size();
            } catch (Throwable e) {
                System.out.println("Error: " + e.getMessage());
                return;
            }
        }

        // Calculate averages
        long avgNonPaginated = nonPaginatedTime / iterations;
        long avgPaginated = paginatedTime / iterations;

        System.out.println(
            "Non-paginated: " +
            formatTime(avgNonPaginated) +
            " (" +
            nonPaginatedAttributes +
            " attributes)"
        );
        System.out.println(
            "Paginated:     " +
            formatTime(avgPaginated) +
            " (" +
            paginatedAttributes +
            " attributes)"
        );

        // Calculate improvement
        double improvement =
            ((double) (avgNonPaginated - avgPaginated) / avgNonPaginated) * 100;
        System.out.println(
            "Improvement:   " +
            String.format("%.1f%%", improvement) +
            " faster with pagination"
        );

        // Performance per attribute
        if (nonPaginatedAttributes > 0 && paginatedAttributes > 0) {
            double nonPagPerAttr =
                (double) avgNonPaginated / nonPaginatedAttributes;
            double pagPerAttr = (double) avgPaginated / paginatedAttributes;
            System.out.println(
                "Per-attribute: " +
                formatTime((long) nonPagPerAttr) +
                " vs " +
                formatTime((long) pagPerAttr)
            );
        }

        System.out.println();
    }

    /**
     * Comprehensive test of multiple endpoints to identify bottlenecks
     */
    private static void testAllEndpointsPerformance(
        BBGraph graph,
        int nodeCount
    ) {
        System.out.println("=== Comprehensive Endpoint Performance Test ===");

        User testUser = graph.create( User.class);
        testUser.setAdmin(true);

        // Get test nodes
        List<BNode> testNodes = new ArrayList<>();
        graph.forEachNode(node -> {
            if (testNodes.size() < 10 && !(node instanceof User)) {
                testNodes.add(node);
            }
        });

        if (testNodes.isEmpty()) {
            System.out.println("No test nodes available");
            return;
        }

        BNode testNode = testNodes.get(0);

        // Test critical endpoints
        testEndpoint("SearchNode", () -> {
            try {
                SearchNode searchEndpoint = new SearchNode(graph);
                ObjectNode input = new ObjectNode(null);
                input.set("query", new TextNode("test"));
                input.set("limit", new IntNode(10));
                return searchEndpoint.exec(
                    input,
                    testUser,
                    null,
                    null,
                    testNode
                );
            } catch (Throwable e) {
                return null;
            }
        });

        testEndpoint("AddNode", () -> {
            try {
                AddNode addEndpoint = new AddNode(graph);
                ObjectNode input = new ObjectNode(null);
                input.set("nodeType", new TextNode("StringNode"));
                return addEndpoint.exec(input, testUser, null, null, testNode);
            } catch (Throwable e) {
                return null;
            }
        });

        testEndpoint("SetValue", () -> {
            try {
                SetValue setEndpoint = new SetValue(graph);
                ObjectNode input = new ObjectNode(null);
                input.set("value", new TextNode("test_value"));
                return setEndpoint.exec(input, testUser, null, null, testNode);
            } catch (Throwable e) {
                return null;
            }
        });

        testEndpoint("Edit", () -> {
            try {
                Edit editEndpoint = new Edit(graph);
                ObjectNode input = new ObjectNode(null);
                return editEndpoint.exec(input, testUser, null, null, testNode);
            } catch (Throwable e) {
                return null;
            }
        });

        testEndpoint("NodeInfo", () -> {
            try {
                NodeInfo infoEndpoint = new NodeInfo(graph);
                ObjectNode input = new ObjectNode(null);
                return infoEndpoint.exec(input, testUser, null, null, testNode);
            } catch (Throwable e) {
                return null;
            }
        });

        testEndpoint("ListExistingNode", () -> {
            try {
                ListExistingNode listEndpoint = new ListExistingNode(graph);
                ObjectNode input = new ObjectNode(null);
                input.set("nodeType", new TextNode("StringNode"));
                input.set("limit", new IntNode(20));
                return listEndpoint.exec(input, testUser, null, null, testNode);
            } catch (Throwable e) {
                return null;
            }
        });

        testEndpoint("Jump", () -> {
            try {
                Jump jumpEndpoint = new Jump(graph);
                ObjectNode input = new ObjectNode(null);
                input.set("target_id", new IntNode(testNode.id()));
                return jumpEndpoint.exec(input, testUser, null, null, testNode);
            } catch (Throwable e) {
                return null;
            }
        });

        testEndpoint("BasicView", () -> {
            try {
                BasicView basicEndpoint = new BasicView(graph);
                ObjectNode input = new ObjectNode(null);
                return basicEndpoint.exec(
                    input,
                    testUser,
                    null,
                    null,
                    testNode
                );
            } catch (Throwable e) {
                return null;
            }
        });

        testEndpoint("Navigator", () -> {
            try {
                Navigator navEndpoint = new Navigator(graph);
                ObjectNode input = new ObjectNode(null);
                return navEndpoint.exec(input, testUser, null, null, testNode);
            } catch (Throwable e) {
                return null;
            }
        });

        testEndpoint("InOutsNivoView", () -> {
            try {
                BNode.InOutsNivoView nivoEndpoint = new BNode.InOutsNivoView(
                    graph
                );
                ObjectNode input = new ObjectNode(null);
                return nivoEndpoint.exec(input, testUser, null, null, testNode);
            } catch (Throwable e) {
                return null;
            }
        });

        testEndpoint("OutDegreeDistribution", () -> {
            try {
                OutDegreeDistribution distEndpoint =
                    new OutDegreeDistribution(graph);
                ObjectNode input = new ObjectNode(null);
                return distEndpoint.exec(input, testUser, null, null, testNode);
            } catch (Throwable e) {
                return null;
            }
        });

        testEndpoint("ClassDistribution", () -> {
            try {
                ClassDistribution classEndpoint =
                    new ClassDistribution(graph);
                ObjectNode input = new ObjectNode(null);
                return classEndpoint.exec(
                    input,
                    testUser,
                    null,
                    null,
                    testNode
                );
            } catch (Throwable e) {
                return null;
            }
        });

        testEndpoint("ExportCSV", () -> {
            try {
                ExportCSV exportEndpoint = new ExportCSV(graph);
                ObjectNode input = new ObjectNode(null);
                input.set("format", new TextNode("csv"));
                return exportEndpoint.exec(
                    input,
                    testUser,
                    null,
                    null,
                    testNode
                );
            } catch (Throwable e) {
                return null;
            }
        });

        testEndpoint("Endpoints", () -> {
            try {
                Endpoints endpointsEndpoint = new Endpoints(graph);
                ObjectNode input = new ObjectNode(null);
                return endpointsEndpoint.exec(
                    input,
                    testUser,
                    null,
                    null,
                    testNode
                );
            } catch (Throwable e) {
                return null;
            }
        });

        System.out.println();
    }

    /**
     * Test a specific endpoint and measure its performance
     */
    private static void testEndpoint(
        String endpointName,
        java.util.function.Supplier<Object> endpointRunner
    ) {
        System.out.print("Testing " + endpointName + "... ");

        // Warm up
        try {
            for (int i = 0; i < 3; i++) {
                endpointRunner.get();
            }
        } catch (Exception e) {
            // Ignore warm-up errors
        }

        // Actual test
        long startTime = System.nanoTime();
        boolean success = false;
        Object result = null;

        try {
            result = endpointRunner.get();
            success = true;
        } catch (Exception e) {
            // Endpoint failed
        }

        long endTime = System.nanoTime();
        long executionTime = endTime - startTime;

        if (success && result != null) {
            System.out.println(formatTime(executionTime) + " ✓");
        } else {
            System.out.println("FAILED or NULL result");
        }
    }

    /**
     * Format nanoseconds to human-readable time
     */
    private static String formatTime(long nanos) {
        if (nanos < 1_000) {
            return nanos + "ns";
        } else if (nanos < 1_000_000) {
            return String.format("%.2fμs", nanos / 1_000.0);
        } else if (nanos < 1_000_000_000) {
            return String.format("%.2fms", nanos / 1_000_000.0);
        } else {
            return String.format("%.2fs", nanos / 1_000_000_000.0);
        }
    }

    /**
     * Benchmark a specific operation
     */
    public static void benchmarkOperation(
        String operationName,
        Runnable operation
    ) {
        System.out.print("Benchmarking " + operationName + "... ");

        // Warm up
        for (int i = 0; i < 10; i++) {
            operation.run();
        }

        // Actual benchmark
        long startTime = System.nanoTime();
        operation.run();
        long endTime = System.nanoTime();

        System.out.println(formatTime(endTime - startTime));
    }

    /**
     * Test memory usage
     */
    public static void printMemoryUsage(String context) {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;

        System.out.println(
            "Memory usage " +
            context +
            ": " +
            formatMemory(usedMemory) +
            " / " +
            formatMemory(totalMemory)
        );
    }

    private static String formatMemory(long bytes) {
        if (bytes < 1024) {
            return bytes + "B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2fKB", bytes / 1024.0);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.2fMB", bytes / (1024.0 * 1024.0));
        } else {
            return String.format("%.2fGB", bytes / (1024.0 * 1024.0 * 1024.0));
        }
    }
}
