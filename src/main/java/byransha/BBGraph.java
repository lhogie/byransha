package byransha;

import byransha.graph.AnyGraph;
import byransha.graph.BVertex;
import byransha.web.EndpointJsonResponse;
import byransha.web.EndpointJsonResponse.dialects;
import byransha.web.EndpointResponse;
import byransha.web.EndpointTextResponse;
import byransha.web.NodeEndpoint;
import byransha.web.TechnicalView;
import byransha.web.View;
import byransha.web.WebServer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpsExchange;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import toools.reflect.Clazz;

public class BBGraph extends BNode {

    public static final Consumer<File> sysoutPrinter = f ->
        System.out.println("writing " + f.getAbsolutePath());
    public final File directory;

    private final ConcurrentMap<Integer, BNode> nodesById;
    private final ConcurrentMap<Class<? extends BNode>, Queue<BNode>> byClass;
    private final ConcurrentMap<Integer, Set<InLink>> incomingReferences =
        new ConcurrentHashMap<>();

    private final AtomicInteger idSequence = new AtomicInteger(1);

    StringNode testString;
    BooleanNode testBoolean;

    @Override
    public String whatIsThis() {
        return "BBGraph: A graph representation for BNodes.";
    }

    public BBGraph(File directory) {
        super(null, 0); // The graph has automatically ID 0
        this.directory = directory;
        this.nodesById = new ConcurrentHashMap<>();
        this.byClass = new ConcurrentHashMap<>();

        accept(this); // self accept
        this.setColor("#ff8c00");
    }

    public List<NodeEndpoint> endpointsUsableFrom(BNode n) {
        List<NodeEndpoint> r = new ArrayList<>();

        for (var v : findAll(NodeEndpoint.class, e -> true)) {
            if (v.getTargetNodeType().isAssignableFrom(n.getClass())) {
                r.add(v);
            }
        }

        r.sort((a, b) -> {
            Class<?> aType = a.getTargetNodeType();
            Class<?> bType = b.getTargetNodeType();

            if (aType.equals(bType)) return 0;
            else if (aType.isAssignableFrom(bType)) return 1;
            else if (bType.isAssignableFrom(aType)) return -1;
            else return aType.getName().compareTo(bType.getName());
        });

        return r;
    }

    public synchronized int nextID() {
        int potentialId;
        do {
            potentialId = idSequence.getAndIncrement();
        } while (potentialId == 0 || nodesById.containsKey(potentialId));
        return potentialId;
    }

    public List<InLink> findRefsTO(BNode searchedNode) {
        Set<InLink> refs = incomingReferences.get(searchedNode.id());
        return refs != null ? new ArrayList<>(refs) : new ArrayList<>();
    }

    public void loadFromDisk(
        Consumer<BNode> newNodeInstantiated,
        BiConsumer<BNode, String> setRelation
    ) {
        instantiateNodes(newNodeInstantiated);

        nodesById
            .values()
            .forEach(n -> {
                if (n instanceof PersistingNode pn) {
                    loadOuts(pn, setRelation);
                }
            });

        int maxId = nodesById.keySet().stream().max(Integer::compare).orElse(0);
        idSequence.set(maxId + 1);
    }

    /*
     * Loads all nodes from all class directories from the disk
     */
    private void instantiateNodes(Consumer<BNode> newNodeInstantiated) {
        File[] files = directory.listFiles();
        if (files == null) return;
        else {
            for (File classDir : files) {
                String className = classDir.getName();
                var nodeClass = (Class<? extends BNode>) Clazz.findClassOrFail(
                    className
                );
                if (nodeClass.equals(WebServer.class)) {
                    System.err.println(
                        "Skipping WebServer class " + nodeClass.getName()
                    );
                    continue;
                }

                for (File nodeDir : Objects.requireNonNull(
                    classDir.listFiles()
                )) {
                    int id = Integer.parseInt(nodeDir.getName().substring(1));

                    // don't create the graph node twice!
                    if (id != 0) {
                        try {
                            var constructor = nodeClass.getConstructor(
                                BBGraph.class,
                                int.class
                            );
                            BNode node = constructor.newInstance(graph, id);
                            newNodeInstantiated.accept(node);
                        } catch (
                            InstantiationException
                            | IllegalAccessException
                            | IllegalArgumentException
                            | InvocationTargetException
                            | SecurityException err
                        ) {
                            throw new RuntimeException(err);
                        } catch (NoSuchMethodException e) {
                            System.out.println(
                                "Warning: No constructor found for class " +
                                nodeClass.getName() +
                                ": " +
                                e.getMessage()
                            );
                        }
                    }
                }
            }
        }
    }

    private void loadOuts(
        PersistingNode node,
        BiConsumer<BNode, String> setRelation
    ) {
        var d = node.outsDirectory();

        if (!d.exists()) return;

        File[] files = d.listFiles();
        if (files == null) {
            System.err.println(
                "Warning: Could not list files in directory: " +
                d.getAbsolutePath()
            );
            return;
        }

        for (var symlink : files) {
            try {
                Path targetFile = Files.readSymbolicLink(symlink.toPath());
                String relationName = targetFile.getFileName().toString();
                var fn = targetFile.getFileName().toString();

                // Check if the filename contains the expected format
                //				int atIndex = fn.indexOf("@");
                //				if (atIndex == -1) {
                //					System.err.println("Warning: Invalid filename format for symlink: " + fn);
                //					continue;
                //				}

                try {
                    int id = Integer.parseInt(fn.substring(1)); // atIndex + 1
                    BNode targetNode = findByID(id);

                    if (targetNode == null) {
                        System.err.println(
                            "Warning: Could not find node with ID: " + id
                        );
                        continue;
                    }

                    try {
                        if (node instanceof ListNode<?>) {
                            ((ListNode<BNode>) node).add(targetNode);
                        } else {
                            try {
                                if (node.hasField(symlink.getName())) {
                                    node.setField(
                                        symlink.getName(),
                                        targetNode
                                    );
                                }
                            } catch (Exception e) {
                                System.err.println(
                                    "Error setting field " +
                                    symlink.getName() +
                                    " for node " +
                                    node +
                                    ": " +
                                    e.getMessage()
                                );
                            }
                        }
                        setRelation.accept(node, relationName);
                    } catch (Exception e) {
                        System.err.println(
                            "Error setting relation " +
                            relationName +
                            " for node " +
                            node +
                            ": " +
                            e.getMessage()
                        );
                    }
                } catch (NumberFormatException e) {
                    System.err.println(
                        "Error: Invalid node ID in filename: " +
                        fn +
                        ": " +
                        e.getMessage()
                    );
                }
            } catch (IOException e) {
                System.err.println(
                    "Error reading symbolic link: " +
                    symlink.getPath() +
                    ": " +
                    e.getMessage()
                );
            } catch (Exception e) {
                System.err.println(
                    "Unexpected error processing symlink " +
                    symlink.getPath() +
                    ": " +
                    e.getMessage()
                );
                e.printStackTrace();
            }
        }
    }

    /**
     * Executes the given consumer for each node in the graph, except WebServer
     * nodes. WebServer nodes are excluded to prevent circular references during
     * serialization. Uses synchronization to prevent
     * ConcurrentModificationException.
     *
     * @param h The consumer to execute for each node
     */
    public void forEachNode(Consumer<BNode> h) {
        for (BNode n : nodesById.values()) {
            if (!(n instanceof WebServer)) {
                h.accept(n);
            }
        }
    }

    public void saveAll(Consumer<File> writingFiles) throws IOException {
        forEachNode(n -> {
            if (n instanceof ValuedNode vn) {
                vn.saveValue(writingFiles);
            }
        });

        forEachNode(n -> {
            if (n instanceof PersistingNode pn) {
                pn.createOutSymLinks(writingFiles);
                pn.createInSymLinks(writingFiles);
            }
        });
    }

    public long countNodes() {
        return nodesById.size();
    }

    synchronized void accept(BNode n) {
        BNode previous;
        Class<? extends BNode> nodeClass = n.getClass();

        previous = nodesById.putIfAbsent(n.id(), n);
        if (previous != null) {
            if (previous != n) {
                throw new IllegalStateException(
                    "can't add node " +
                    n +
                    " because its ID " +
                    n.id() +
                    " is already taken by: " +
                    previous
                );
            }
            return;
        }

        byClass
            .computeIfAbsent(nodeClass, k -> new ConcurrentLinkedQueue<>())
            .add(n);

        buildIncomingReferencesForNode(n);

        if (n instanceof NodeEndpoint ne) {
            var alreadyInClass = findEndpoint(ne.getClass());
            if (alreadyInClass != null && alreadyInClass != ne) {
                nodesById.remove(n.id());
                Queue<BNode> queue = byClass.get(nodeClass);
                if (queue != null) queue.remove(n);
                throw new IllegalArgumentException(
                    "Adding " +
                    ne +
                    ", endpoint with same class '" +
                    ne.getClass().getName() +
                    "' already there: " +
                    alreadyInClass
                );
            }

            var alreadyInName = findEndpoint(ne.name());
            if (alreadyInName != null && alreadyInName != ne) {
                nodesById.remove(n.id());
                Queue<BNode> queue = byClass.get(nodeClass);
                if (queue != null) queue.remove(n);
                throw new IllegalArgumentException(
                    "Adding " +
                    ne +
                    ", endpoint with same name '" +
                    ne.name() +
                    "' already there: " +
                    alreadyInName.getClass().getName()
                );
            }
        }

        if (n instanceof PersistingNode pn && pn.directory() != null) {
            try {
                pn.createOutSymLinks(BBGraph.sysoutPrinter);
            } catch (Exception e) {
                System.err.println(
                    "Error creating symlinks for node " +
                    n.id() +
                    " after accepting: " +
                    e.getMessage()
                );
            }
        }
    }

    private void buildIncomingReferencesForNode(BNode n) {
        n.forEachOut((role, outNode) -> {
            if (outNode != null) {
                addIncomingReference(n, role, outNode);
            }
        });
    }

    private void addIncomingReference(BNode from, String role, BNode to) {
        incomingReferences
            .computeIfAbsent(to.id(), k -> ConcurrentHashMap.newKeySet())
            .add(new InLink(role, from));
    }

    private void removeIncomingReference(BNode from, String role, BNode to) {
        Set<InLink> refs = incomingReferences.get(to.id());
        if (refs != null) {
            refs.removeIf(
                link -> link.source().equals(from) && link.role().equals(role)
            );
            if (refs.isEmpty()) {
                incomingReferences.remove(to.id());
            }
        }
    }

    public void updateEdge(BNode from, String role, BNode oldTo, BNode newTo) {
        if (oldTo != null) {
            removeIncomingReference(from, role, oldTo);
        }
        if (newTo != null) {
            addIncomingReference(from, role, newTo);
        }
    }

    public BNode root() {
        return this;
    }

    public void delete() {
        delete(directory);
    }

    private void delete(File d) {
        if (d.isDirectory()) {
            File[] files = d.listFiles();
            if (files != null) {
                for (var c : files) {
                    delete(c);
                }
            } else {
                System.err.println(
                    "Warning: Could not list files in directory: " +
                    d.getAbsolutePath()
                );
            }
        }

        System.out.println("Deleting " + d);
        boolean success = d.delete();
        if (!success) {
            System.err.println(
                "Warning: Failed to delete " + d.getAbsolutePath()
            );
            // Try to determine why deletion failed
            if (!d.exists()) {
                System.err.println("  File does not exist");
            } else if (!d.canWrite()) {
                System.err.println("  File is not writable");
            } else if (
                d.isDirectory() &&
                d.list() != null &&
                Objects.requireNonNull(d.list()).length > 0
            ) {
                System.err.println("  Directory is not empty");
            }
        }
    }

    public BNode findByID(int id) {
        return nodesById.get(id);
    }

    public synchronized <C extends BNode> C find(
        Class<C> nodeClass,
        Predicate<C> p
    ) {
        List<C> l = findAll(nodeClass, p);
        return l.isEmpty() ? null : l.getFirst();
    }

    public <C extends BNode> List<C> findAll(
        Class<C> nodeClass,
        Predicate<C> p
    ) {
        List<C> r = new ArrayList<>();

        Queue<BNode> directNodes = byClass.get(nodeClass);
        if (directNodes != null) {
            for (BNode node : directNodes) {
                C nn = nodeClass.cast(node);
                if (p.test(nn)) {
                    r.add(nn);
                }
            }
        }

        if (nodeClass != BNode.class) {
            for (Map.Entry<
                Class<? extends BNode>,
                Queue<BNode>
            > entry : byClass.entrySet()) {
                if (
                    entry.getKey() != nodeClass &&
                    nodeClass.isAssignableFrom(entry.getKey())
                ) {
                    for (BNode node : entry.getValue()) {
                        C nn = nodeClass.cast(node);
                        if (p.test(nn)) {
                            r.add(nn);
                        }
                    }
                }
            }
        }

        return r;
    }

    public List<User> users() {
        return nodesById
            .values()
            .stream()
            .filter(User.class::isInstance)
            .map(User.class::cast)
            .toList();
    }

    public <N extends BNode, NE extends NodeEndpoint<N>> NE findEndpoint(
        Class<NE> c
    ) {
        return find(c, e -> true);
    }

    public NodeEndpoint findEndpoint(String name) {
        return find(NodeEndpoint.class, e -> e.name().equalsIgnoreCase(name));
    }

    public static class DBView
        extends NodeEndpoint<BBGraph>
        implements TechnicalView {

        @Override
        public String whatItDoes() {
            return "gives info on the graph";
        }

        public DBView(BBGraph g) {
            super(g);
        }

        @Override
        public EndpointResponse exec(
            ObjectNode input,
            User user,
            WebServer webServer,
            HttpsExchange exchange,
            BBGraph node
        ) throws Throwable {
            return new EndpointTextResponse("text/html", pw -> {
                pw.println("<ul>");

                pw.println("<li>" + graph.countNodes() + " nodes");

                Set<String> classNames = new HashSet<>();
                graph.forEachNode(n -> classNames.add(n.getClass().getName()));
                pw.println("<li>Node classes: <ul>");
                classNames
                    .stream()
                    .sorted()
                    .forEach(cn -> pw.println("<li>" + cn + "</li>"));
                pw.println("</ul></li>");

                var users = graph.users();
                pw.println(
                    "<li>" +
                    users.size() +
                    " users: " +
                    users
                        .stream()
                        .map(u -> u.name.get())
                        .toList()
                );
                pw.println("</ul>");
            });
        }

        @Override
        public boolean sendContentByDefault() {
            return false;
        }
    }

    public static class GraphNivoView extends NodeEndpoint<BBGraph> {

        @Override
        public String whatItDoes() {
            return "gives a NIVO text representing the graph";
        }

        public GraphNivoView(BBGraph db) {
            super(db);
        }

        public GraphNivoView(BBGraph db, int id) {
            super(db, id);
        }

        @Override
        public EndpointResponse exec(
            ObjectNode in,
            User user,
            WebServer webServer,
            HttpsExchange exchange,
            BBGraph db
        ) {
            var g = new AnyGraph();

            Map<Integer, BVertex> vertexCache = new HashMap<>();
            db.forEachNode(v -> {
                if (v.canSee(user)) {
                    BVertex vertex = v.toVertex();
                    g.addVertex(vertex);
                    vertexCache.put(v.id(), vertex);
                }
            });

            db.forEachNode(v -> {
                if (v.canSee(user)) {
                    BVertex sourceVertex = vertexCache.get(v.id());
                    if (sourceVertex != null) {
                        v.forEachOut((s, o) -> {
                            if (o.canSee(user)) {
                                BVertex targetVertex = vertexCache.get(o.id());
                                if (targetVertex != null) {
                                    var arc = g.newArc(
                                        sourceVertex,
                                        targetVertex
                                    );
                                    arc.label = s;
                                }
                            }
                        });
                    }
                }
            });

            return new EndpointJsonResponse(
                g.toNivoJSON(),
                dialects.nivoNetwork
            );
        }
    }

    @Override
    public String prettyName() {
        return "graph";
    }

    public HashSet<Class<? extends BNode>> classes() {
        return new HashSet<>(byClass.keySet());
    }

    public static class ClassDistribution
        extends NodeEndpoint<BBGraph>
        implements View {

        public ClassDistribution(BBGraph db) {
            super(db);
        }

        public ClassDistribution(BBGraph db, int id) {
            super(db, id);
        }

        @Override
        public String whatItDoes() {
            return "shows distributed for out nodes";
        }

        @Override
        public EndpointResponse exec(
            ObjectNode in,
            User user,
            WebServer webServer,
            HttpsExchange exchange,
            BBGraph g
        ) throws Throwable {
            var d = new Byransha.Distribution<String>();
            g.forEachNode(n -> d.addOccurence(n.getClass().getName()));
            return new EndpointJsonResponse(d.toJson(), dialects.distribution);
        }

        @Override
        public boolean sendContentByDefault() {
            return false;
        }
    }
}
