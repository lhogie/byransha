package byransha;

import byransha.graph.AnyGraph;
import byransha.graph.BVertex;
import byransha.web.*;
import byransha.web.EndpointJsonResponse.dialects;
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

    private ConcurrentMap<Integer, BNode> nodesById;
    private ConcurrentMap<Class<? extends BNode>, Queue<BNode>> byClass;

    private final AtomicInteger idSequence = new AtomicInteger(1);
    private volatile boolean loading = false;

    StringNode testString;
    BooleanNode testBoolean;
    private User admin, system;

    @Override
    public String whatIsThis() {
        return "BBGraph: A graph representation for BNodes.";
    }

    public BBGraph(User user) {
        this((File) null, user);
    }

    public BBGraph(File directory, User user) {
        super(null, user); // The graph has automatically ID 0
        // Ensure the graph self-node is accepted early

        if (user!=null)
            throw new IllegalArgumentException();

        accept(this,user = admin = new User(this, null, "admin", "admin")); // self accept
        accept(this,user = admin = new User(this, null, "system", "system")); // self accept
        this.directory = directory;

        if (this.nodesById == null) this.nodesById = new ConcurrentHashMap<>();

        if (this.byClass == null) this.byClass = new ConcurrentHashMap<>();
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
        List<InLink> refs = new ArrayList<>();
        for (BNode node : nodesById.values()) {
            node.forEachOut((role, target) -> {
                if (target != null && target.equals(searchedNode)) {
                    refs.add(new InLink(role, node));
                }
            });
        }
        return refs;
    }

    public synchronized void loadFromDisk(
        Consumer<BNode> newNodeInstantiated,
        BiConsumer<BNode, String> setRelation,
        User user
    ) {
        this.loading = true;
        // Pre-scan disk to set the idSequence high enough to avoid collisions

        // if constructors create nodes during loading
        int maxIdOnDisk = 0;
        if (directory != null) {
            File[] classDirs = directory.listFiles();
            if (classDirs != null) {
                for (File classDir : classDirs) {
                    if (!classDir.isDirectory()) continue;
                    File[] nodeDirs = classDir.listFiles();
                    if (nodeDirs == null) continue;
                    for (File nodeDir : nodeDirs) {
                        if (!nodeDir.isDirectory()) continue;
                        String dn = nodeDir.getName();
                        if (dn.length() > 1) {
                            try {
                                int id = Integer.parseInt(dn.substring(1));
                                if (id > maxIdOnDisk) maxIdOnDisk = id;
                            } catch (NumberFormatException ignore) {}
                        }
                    }
                }
            }
        }
        idSequence.set(maxIdOnDisk + 1);

        instantiateNodes(newNodeInstantiated);

        nodesById
            .values()
            .forEach(n -> {
                if (n.isPersisting()) {
                    loadOuts(n, setRelation, user);
                }
            });

        int maxId = nodesById
            .keySet()
            .stream()
            .max(Integer::compare)
            .orElse(maxIdOnDisk);

        idSequence.set(maxId + 1);

        this.loading = false;
    }

    /*

     * Loads all nodes from all class directories from the disk

     */

    private synchronized void instantiateNodes(
        Consumer<? super BNode> newNodeInstantiated
    ) {
        if (directory == null) return;
        File[] files = directory.listFiles();

        if (files == null) return;
        else {
            for (File classDir : files) {
                if (!classDir.isDirectory()) continue;

                String className = classDir.getName();

                Class<?> foundClass = Clazz.findClassOrFail(className);
                if (!BNode.class.isAssignableFrom(foundClass)) {
                    continue;
                }
                var nodeClass = (Class<? extends BNode>) foundClass;
                if (WebServer.class.isAssignableFrom(nodeClass)) {
                    System.err.println(
                        "Skipping WebServer class " + nodeClass.getName()
                    );

                    continue;
                }

                for (File nodeDir : Objects.requireNonNull(
                    classDir.listFiles()
                )) {
                    if (!nodeDir.isDirectory()) continue;

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

    private void loadOuts(BNode node, BiConsumer<BNode, String> setRelation, User user) {
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
                            ((ListNode<BNode>) node).add(targetNode, user);
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

    public long countNodes() {
        return nodesById.size();
    }

    public void deleteNode(BNode node) {
        if (node.ins().size() <= 1) {
            deleteNodeDirectory(node);
            graph.removeFromGraph(node);

            node.forEachOut((name, child) -> {
                if (child instanceof BBGraph || child instanceof Cluster)
                    return;
                deleteNode(child);
            });
        }
        else {
            System.out.println("Node " + node.id() + " still in use, not deleted.");
        }
    }

    private void deleteNodeDirectory(BNode node) {
        File dir = node.directory();
        if (dir != null && dir.exists()) {
            try {
                Files.walk(dir.toPath())
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
                System.out.println("Deleted directory " + dir.getAbsolutePath());
            } catch (IOException e) {
                System.err.println("Failed to delete " + dir.getAbsolutePath() + ": " + e.getMessage());
            }
        }
    }

    public List<BNode> getAllDeleteNodes(BNode node) {
        List<BNode> result = new ArrayList<>();
        node.forEachOut((name, child) -> {
            if (child instanceof BBGraph || child instanceof Cluster) return; // Skip graphs, they are not deleted here
            result.add(child);
            result.addAll(getAllDeleteNodes(child));
        });
        return result;
    }

    synchronized <N extends BNode> void integrate(N n, User user) {
        // Ensure core maps are initialized even during early construction
        if (this.nodesById == null) this.nodesById = new ConcurrentHashMap<>();

        if (this.byClass == null) this.byClass = new ConcurrentHashMap<>();

        n.graph = this;

        if (n == this) {
            nodesById.put(0, n);
            byClass
                .computeIfAbsent(BBGraph.class, k ->
                    new ConcurrentLinkedQueue<>()
                )
                .add(n);
            return;
        }

        if (n instanceof NodeEndpoint ne) {
            var alreadyInName = findEndpoint(ne.name());

            if (alreadyInName != null) {
                throw new IllegalArgumentException(
                    "Adding " +
                    ne +
                    ", endpoint with same name '" +
                    ne.name() +
                    "' already there: " +
                    alreadyInName.getClass().getName()
                );
            }
            n.setColor("#00fff5", user);
        }

        Class<? extends BNode> nodeClass = n.getClass();
        if(!(n instanceof Cluster)) n.createOrAssignCluster(user);

        byClass
            .computeIfAbsent(nodeClass, k -> new ConcurrentLinkedQueue<>())
            .add(n);

        BNode previous = nodesById.putIfAbsent(n.id(), n);

        if (previous != null && previous != n) throw new IllegalStateException(
            "can't add node " +
            n +
            " because its ID " +
            n.id() +
            " is already taken by: " +
            previous
        );

        if (n.isPersisting() && !loading) {
            n.save(BBGraph.sysoutPrinter);
        }
    }

    synchronized <N extends BNode> N accept(N n, User user) {
        n.initialized(user);
        return n;
    }

    public void removeFromGraph(BNode n) {
        if (n == null) return;
        graph.nodesById.remove(n.id());
        Queue<BNode> classQueue = byClass.get(n.getClass());
        if (classQueue != null) {
            classQueue.remove(n);
            if (classQueue.isEmpty()) {
                byClass.remove(n.getClass());
            }
        }
        Cluster c  = graph.find(Cluster.class, cl -> cl.typeOfCluster.equals(n.getClass()));
        if (c != null)
            c.remove(n);
        else
            System.err.println("Warning: No cluster found for node " + n.getClass().getSimpleName());
//        n.graph = null;
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

    public User admin() {
        return admin;
    }

    public User systemUser() {
        return system;
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
