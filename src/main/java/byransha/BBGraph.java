package byransha;

import byransha.graph.AnyGraph;
import byransha.graph.BVertex;
import byransha.web.*;
import byransha.web.EndpointJsonResponse.dialects;
import byransha.web.endpoint.*;
import byransha.web.view.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpsExchange;

import java.io.BufferedWriter;
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
import java.util.function.IntConsumer;
import java.util.function.Predicate;

import toools.reflect.Clazz;

public class BBGraph extends BNode {
    public enum LOGTYPE { FILE_WRITE, FILE_READ, WARNING, ERROR };

    public static class Logger implements BiConsumer<LOGTYPE, String> {
        public boolean stdout = false;
        static File logFile = new File("./log.txt");
        static BufferedWriter finalWriter;

        static {
            try {
                finalWriter = new BufferedWriter(
                        Files.newBufferedWriter(logFile.toPath(), java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND)
                );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void accept(LOGTYPE logtype, String s) {
            if (stdout){
                //System.out.println(logtype.name() + "\t" + s);
                try {
                    finalWriter.write(logtype.name() + "\t" + s + "\n");
                    finalWriter.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static Logger logger = new Logger();
    public final File directory;

    ConcurrentMap<Integer, BNode> nodesById  = new ConcurrentHashMap<>();
    ConcurrentMap<Class<? extends BNode>, Queue<BNode>> byClass  = new ConcurrentHashMap<>();

    private final AtomicInteger idSequence = new AtomicInteger(1);

    private final User admin, system;
    public UserApplication application;
    private final WebServer webServer;
    public volatile boolean isLoading;

    public BBGraph(File directory, Map<String, String> argMap)
            throws Exception {
        super(null, null, InstantiationInfo.notPersisting);

        var appClass = (Class<? extends UserApplication>) Class.forName(
                argMap.remove("appClass")
        );

        this.directory = directory;

        if (!directory.exists())
            directory.mkdirs();

        nodesById.put(0, this);
        byClass
                .computeIfAbsent(BBGraph.class, k ->
                        new ConcurrentLinkedQueue<>()
                )
                .add(this);

        System.out.println("Starting to load DB from " + directory);
        loadFromDisk(
                n -> logger.accept(LOGTYPE.FILE_READ, "loading node " + n),
                (n, s) -> System.out.println("loading arc " + n + ", " + s),
                systemUser()
        );
        System.out.println("DB loaded, " + nodesById.size() + " nodes in memory.");

        this.application = appClass.getConstructor(BBGraph.class, User.class, InstantiationInfo.class).newInstance(this, admin(), InstantiationInfo.notPersisting);
        int port = Integer.parseInt(argMap.getOrDefault("-port", "8080"));
        this.webServer = new WebServer(this, port);

        createEndpoints(g);
        new JVMNode(g);
        new Byransha(g, systemUser());
        new OSNode(g);
        this.admin = new User(this, null, InstantiationInfo.notPersisting, "admin", "admin"); // self accept
        this.system = new User(this, null, InstantiationInfo.notPersisting, "system", ""); // self accept
        new User(g, systemUser(), InstantiationInfo.notPersisting, "user", "test");
        new SearchForm(g, systemUser(), InstantiationInfo.notPersisting );

        g.forEachNode(node -> {
            node.findCluster(system);
        });
        System.out.println("Cluster done");

        endOfConstructor();

        logger.accept(LOGTYPE.FILE_READ, "loading DB from " + directory);
    }


    private void createEndpoints(BBGraph g) {
        new NodeInfo(this);
        new Views(this);
        new Jump(this);
        new Endpoints(this);
        new JVMNode.Kill(this);
        var n = new Authenticate(g, webServer.sessionStore);
        var l = new Logout(g, webServer.sessionStore);
        new WebServer.EndpointCallDistributionView(this);
        new WebServer.Info(this);
        new WebServer.Logs(this);
        new BasicView(this);
        new CharExampleXY(this);
        new User.UserView(this);
        new BBGraph.GraphNivoView(this);
        new OSNode.View(this);
        new JVMNode.View(this);
        new BNode.InOutsNivoView(this);
        new ModelGraphivzSVGView(this);
        new ModelMermaidView(this);
        new Navigator(this);
        new OutDegreeDistribution(this);
        new byransha.ClassDistribution(this);
        new ModelDOTView(this);
        new ToStringView(this);
        new NodeEndpoints(this);
        new SetValue(this);
        new AnyGraph.Classes(this);
        new User.History(this);
        new UI(g, g.systemUser(), InstantiationInfo.notPersisting);
        new UI.getProperties(this);
        new Summarizer(this);
        new LoadImage(this);
        new ClassInformation(this);
        new ClassAttributeField(this);
        new AddNode(this);
        new AddExistingNode(this);
        new ListExistingNode(this);
        new SearchNode(this);
        new ExportCSV(this);
        new RemoveFromList(this);
        new RemoveNode(this);
        new ColorNodeView(this);
        new ListChildClasses(this);
    }

    @Override
    protected void createOuts(User creator) {
        super.createOuts(creator);
    }

    @Override
    public String whatIsThis() {
        return "a graph";
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
            node.forEachOutField((role, target) -> {
                if (target != null && target == searchedNode) {
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
        isLoading = true;
        int maxIdOnDisk = findMaxIdInDirectory(directory);

        if (idSequence.get() < maxIdOnDisk) {
            idSequence.set(maxIdOnDisk + 1);
        }

        instantiateNodes(newNodeInstantiated);

        nodesById
                .values()
                .forEach(n -> {
                    if (n.isPersisting()) {
                        connectOutsToNode(n, setRelation, user);
                    }
                });

        nodesById
                .values()
                .forEach(n -> {
                    if (n.isPersisting()) {
                        n.nodeConstructed(user);
                    }
                });

        int maxId = nodesById
                .keySet()
                .stream()
                .max(Integer::compare)
                .orElse(maxIdOnDisk);

        if (idSequence.get() < maxId) {
            idSequence.set(maxId + 1);
        }

        isLoading = false;
    }

    private int findMaxIdInDirectory(File rootDir) {
        int maxId = 0;
        File[] classDirectories = rootDir.listFiles();

        if (classDirectories != null) {
            for (File classDir : classDirectories) {
                if (classDir.isDirectory()) {
                    //maxId = Math.max(maxId, scanClassDirectory("", classDir));

                    maxId = Math.max(maxId,
                            Arrays.stream(Objects.requireNonNull(classDir.listFiles())).map(
                                            f -> {
                                                if (f.isDirectory() && f.getName().matches("\\d+")) {
                                                    return Integer.parseInt(f.getName());
                                                }
                                                return 0;
                                            }
                                    )
                                    .max(Integer::compare)
                                    .orElse(0)
                    );
                }
            }
        }

        return maxId;
    }
//
//    private int scanClassDirectory(String idPrefix, File dir) {
//        int maxId = 0;
//        File[] files = dir.listFiles();
//
//        if (files != null) {
//            for (File file : files) {
//                if (file.isDirectory()) {
//                    String currentId = idPrefix + file.getName();
//
//                    if (new File(file, "outs").exists()) {
//                        try {
//                            maxId = Math.max(maxId, Integer.parseInt(currentId));
//                        } catch (NumberFormatException e) {
//                            // Ignore non-numeric directory names
//                        }
//                    }
//
//                    maxId = Math.max(maxId, scanClassDirectory(currentId, file));
//                }
//            }
//        }
//
//        return maxId;
//    }

    /*

     * Loads all nodes from all class directories from the disk

     */

    private synchronized void instantiateNodes(
            Consumer<? super BNode> newNodeInstantiated
    ) {
        File[] files = directory.listFiles();

        if (files == null) return;
        else {
            for (File classDir : files) {
                if (!classDir.isDirectory()) continue;

                String className = classDir.getName();

                Class<?> foundClass = Clazz.findClassOrFail(className);
                if (!BNode.class.isAssignableFrom(foundClass)) {
                    throw new IllegalStateException(foundClass.getName());
                }

                var nodeClass = (Class<? extends BNode>) foundClass;

                try {
                    var constructor = nodeClass.getConstructor(
                            BBGraph.class,
                            User.class,
                            InstantiationInfo.class
                    );


                    listNodeDirectories("", classDir, id -> {
                        // don't create the graph node twice!
                        if (id != 0) {
                            try {
                                this.logger.accept(LOGTYPE.FILE_READ, "Instantiating node of class " + className + " with ID " + id + "\n");
                                BNode node = constructor.newInstance(g, systemUser(), new InstantiationInfo.IDInfo(id));
                                newNodeInstantiated.accept(node);
                            } catch (
                                    InstantiationException | IllegalAccessException | IllegalArgumentException |
                                    InvocationTargetException | SecurityException err
                            ) {
                                    throw new RuntimeException(
                                        "Error instantiating node of class " + className + " with ID " + id,
                                        err
                                );
                            }
                        }
                    });
                }
                catch (NoSuchMethodException e) {
                    throw new IllegalStateException(
                            "Class " + className + " does not have the required constructor",
                            e
                    );
                }
            }
        }
    }

    private void listNodeDirectories(String idPrefix, File classDir, IntConsumer c) {
        for (File file : Objects.requireNonNull(classDir.listFiles())) {
            if (file.isDirectory()) {
                var id = idPrefix +  file.getName();

                if (new File(file, "outs").exists()){ // we found a node
                    c.accept(Integer.parseInt(id));
                }

                //listNodeDirectories(id, file, c);
            }
        }
    }

    private void connectOutsToNode(BNode node, BiConsumer<BNode, String> setRelation, User user) {
        try (var lines = Files.lines(node.outsFile().toPath())) {
            lines.forEach((l -> {
                var a = l.split(":");

                try {
                    var field = Utils.findField(node.getClass(), a[0]);
                    field.setAccessible(true);
                    var target = g.nodesById.get(Integer.valueOf(a[1]));
                    field.set(node, target);
                } catch (IllegalAccessException | NoSuchFieldException err) {
                    throw new RuntimeException("Error setting relation for node " + node + ": " + a[0] + " -> " + a[1], err);
                }
            }));
        } catch (IOException err) {
            throw new RuntimeException(err);
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
            g.removeFromGraph(node);

            node.forEachOutField((name, child) -> {
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
        node.forEachOutField((name, child) -> {
            if (child instanceof BBGraph || child instanceof Cluster) return; // Skip graphs, they are not deleted here
            result.add(child);
            result.addAll(getAllDeleteNodes(child));
        });
        return result;
    }


    public void removeFromGraph(BNode n) {
        if (n == null) return;
        g.nodesById.remove(n.id());
        Queue<BNode> classQueue = byClass.get(n.getClass());
        if (classQueue != null) {
            classQueue.remove(n);
            if (classQueue.isEmpty()) {
                byClass.remove(n.getClass());
            }
        }
        Cluster c  = g.find(Cluster.class, cl -> cl.typeOfCluster.equals(n.getClass()));
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

                pw.println("<li>" + g.countNodes() + " nodes");

                Set<String> classNames = new HashSet<>();
                g.forEachNode(n -> classNames.add(n.getClass().getName()));
                pw.println("<li>Node classes: <ul>");
                classNames
                        .stream()
                        .sorted()
                        .forEach(cn -> pw.println("<li>" + cn + "</li>"));
                pw.println("</ul></li>");

                var users = g.users();
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
            endOfConstructor();

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
                        v.forEachOutField((s, o) -> {
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
