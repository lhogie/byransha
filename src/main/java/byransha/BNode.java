package byransha;

import byransha.annotations.Max;
import byransha.annotations.Min;
import byransha.annotations.Pattern;
import byransha.annotations.Required;
import byransha.annotations.Size;
import byransha.graph.AnyGraph;
import byransha.graph.BVertex;
import byransha.labmodel.model.v0.BusinessNode;
import byransha.web.*;
import byransha.web.EndpointJsonResponse.dialects;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sun.net.httpserver.HttpsExchange;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import toools.gui.Utilities;
import toools.reflect.Clazz;

public abstract class BNode {
    public BBGraph g;
    private final int id;
    public ColorNode color;
    //    public boolean persisting = false;
    public Cluster cluster;
    //public StringNode comment;
    private CountDownLatch constructionMonitor;
    private boolean persisting ;

    public sealed interface InstantiationInfo permits InstantiationInfo.IDInfo, InstantiationInfo.PersistenceInfo {
        record IDInfo(int value) implements InstantiationInfo {}
        record PersistenceInfo(boolean value) implements InstantiationInfo {}

        PersistenceInfo persisting = new PersistenceInfo(true);
        PersistenceInfo notPersisting = new PersistenceInfo(false);
    }

    protected BNode(BBGraph g, User creator, InstantiationInfo parms) {
        this.constructionMonitor = new CountDownLatch(bnodeDepth());

        if (g == null) {
            this.id = 0;
            this.g = (BBGraph) this;
        } else {
            this.g = g;

            if (parms instanceof InstantiationInfo.IDInfo i) {
                this.id = i.value();
                this.persisting = true;
            } else if (parms instanceof InstantiationInfo.PersistenceInfo i) {
                this.id = g.nextID();
                this.persisting = i.value();
                createOuts(creator);
            } else {
                throw new IllegalArgumentException(
                        "InstantiationInfo must be either IDInfo or PersistenceInfo, got: " + parms.getClass().getName()
                );
            }

            if (this instanceof NodeEndpoint ne) {
                var alreadyInName = this.g.findEndpoint(ne.name());

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
                this.setColor("#00fff5", creator);
            }

            Class<? extends BNode> nodeClass = this.getClass();
            this.g.byClass
                    .computeIfAbsent(nodeClass, k -> new ConcurrentLinkedQueue<>())
                    .add(this);

            BNode previous = this.g.nodesById.putIfAbsent(this.id(), this);

            if (previous != null && previous != this) throw new IllegalStateException(
                    "can't add node " +
                            this +
                            " because its ID " +
                            this.id() +
                            " is already taken by: " +
                            previous
            );

            var cluster = findCluster(creator);

            if (cluster != null){
                this.cluster = cluster;
                cluster.add(this, creator);
            }

            new Thread(() -> {
                // waits 1s
                try {
                    var confirmed = constructionMonitor.await(5, TimeUnit.SECONDS);

                    if (confirmed) {
                        if (parms instanceof InstantiationInfo.PersistenceInfo i) {
                            if (isPersisting()) {
                                save();
                            }

                            nodeConstructed(creator);
                        }
                    } else {
                        throw new InterruptedException();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    new Exception(
                            "Node " + BNode.this + " was not initialized properly, construction monitor timed out. Expected " +
                                    bnodeDepth() + " confirmations but got " + (bnodeDepth() - constructionMonitor.getCount()) +  ". Did you explicitely call endOfConstructor() ?????"
                    ).printStackTrace();
                } catch (IOException e) {
                    throw new RuntimeException(
                            "Error while saving node " + this + " to disk: " + e.getMessage(), e
                    );
                } finally {
                    constructionMonitor = null;
                }
            })
                    .start();
        }
    }

    protected abstract void createOuts(User creator);

    protected void nodeConstructed(User user) {
        // This method can be overridden by subclasses to perform additional initialization
    }

    private synchronized Cluster findCluster(User creator) {
        if (!(this instanceof BusinessNode)) return null;

        var cluster = g.find(Cluster.class, c ->
                c.getTypeOfCluster() == this.getClass());

        if (cluster == null) {
            cluster = new Cluster(g, creator, InstantiationInfo.notPersisting);
            cluster.setTypeOfCluster(this.getClass());
        }

        return cluster;
    }

    public void setColor(String newColor, User creator) {
        g.findAll(ColorNode.class, n -> {
            if (n.getAsString().equals(newColor.toString())) {
                this.color = n;
                return true;
            }
            return false;
        });

        if (this.color == null || !this.color.getAsString().equals(newColor)) {
            this.color = new ColorNode(g, creator, InstantiationInfo.persisting);
            this.color.set(newColor, creator);
        }
    }

    public abstract String whatIsThis();

    public record InLink(String role, BNode source) {
        @Override
        public String toString() {
            return source + "." + role;
        }
    }

    public List<InLink> ins() {
        return g.findRefsTO(this);
    }

    private static List<Field> getOutNodeFields(BNode node) {
        List<Field> fields = new ArrayList<>();

        for (var c : Clazz.bfs(node.getClass())) {
            for (var f : c.getDeclaredFields()) {
                if ((f.getModifiers() & Modifier.STATIC) != 0) continue;
                f.setAccessible(true);

                try {
                    if (Out.class == f.getType()) {
                        f.setAccessible(true);
                        fields.add(f);
                    }
                    else if (BNode.class.isAssignableFrom(f.getType()) || f.get(node) instanceof BNode) {
                        f.setAccessible(true);
                        fields.add(f);
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return Collections.unmodifiableList(fields);
    }

    public void forEachOutField(BiConsumer<String, BNode> consumer) {
        getOutNodeFields(this).forEach(f -> {
            try {
                var outNode = (BNode) f.get(this);

                if (outNode != null) {
                    consumer.accept(f.getName(), outNode);
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        });
    }

    public void forEachOut(Consumer<BNode> consumer) {
        forEachOutField((name, n) -> consumer.accept(n));
    }

    public void forEachIn(BiConsumer<String, BNode> consumer) {
        ins().forEach(r -> consumer.accept(r.role, r.source));
    }

    public boolean isLeaf() {
        return outDegree() == 0;
    }

    public void bfs(Consumer<BNode> consumer) {
        search(consumer, List::removeFirst);
    }

    public void dfs(Consumer<BNode> consumer) {
        search(consumer, List::removeLast);
    }

    private void search(
            Consumer<BNode> consumer,
            Function<List<BNode>, BNode> producer
    ) {
        List<BNode> q = new ArrayList<>();
        BNode c = this;
        q.add(c);
        var visited = new HashSet<BNode>();

        while (!q.isEmpty()) {
            c = producer.apply(q);
            consumer.accept(c);
            c.forEachOutField((f, n) -> {
                if (!visited.contains(n)) {
                    visited.add(n);
                    q.add(n);
                }
            });
        }
    }

    public List<BNode> bfs2list() {
        List<BNode> r = new ArrayList<>();
        bfs(r::add);
        return r;
    }

    private LinkedHashMap<String, BNode> outsCache;
    private boolean outsCacheDirty = true;

    public LinkedHashMap<String, BNode> outs() {
        if (outsCacheDirty || outsCache == null) {
            outsCache = new LinkedHashMap<String, BNode>();
            forEachOutField(outsCache::put);
            outsCacheDirty = false;
        }
        return new LinkedHashMap<>(outsCache);
    }

    protected void invalidateOutsCache() {
        outsCacheDirty = true;
    }


    public int outDegree() {
        return outs().size();
    }

    protected void onEdgeChanged(
            String fieldName,
            BNode oldTarget,
            BNode newTarget
    ) {}

    public void remove() {
        invalidateOutsCache();
    }

    public List<SearchResult> search(String query) {
        var r = new ArrayList<SearchResult>();
        bfs(n ->
                r.add(new SearchResult(query, n, n.distanceToSearchString(query)))
        );
        Collections.sort(r);
        return r;
    }

    public int distanceToSearchString(String s) {
        return 1;
    }

    public boolean canSee(User user) {
        return true;
    }

    public boolean canEdit(User user) {
        return !isReadOnly();
    }

    public boolean matches(NodeEndpoint v) {
        return v.getTargetNodeType().isAssignableFrom(getClass());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + id();
    }

    public final int id() {
        return id;
    }

    @Override
    public final int hashCode() {
        return System.identityHashCode(this);
    }

    @Override
    public final boolean equals(Object obj) {
        return this == obj;
    }

    protected boolean hasField(String name) {
        for (var c : Clazz.bfs(getClass())) {
            for (var f : c.getDeclaredFields()) {
                if ((f.getModifiers() & Modifier.STATIC) != 0) continue;

                if (f.getName().equals(name)) {
                    return true;
                }
            }
        }

        return false;
    }

    public Field getFields(int id) {
        BNode node = g.findByID(id);
        for (Map.Entry<String, BNode> entry : this.outs().entrySet()) {
            if (entry.getValue() == node) {
                for (var c : Clazz.bfs(getClass())) {
                    for (var f : c.getDeclaredFields()) {
                        if ((f.getModifiers() & Modifier.STATIC) != 0) continue;
                        if (f.getName().equals(entry.getKey())) {
                            f.setAccessible(true);
                            return f;
                        }
                    }
                }
            }
        }
        return null;
    }

    public void setField(String name, BNode targetNode) {
        for (var c : Clazz.bfs(getClass())) {
            for (var f : c.getDeclaredFields()) {
                if ((f.getModifiers() & Modifier.STATIC) != 0) continue;
                if (f.getName().equals(name)) {
                    try {
                        f.setAccessible(true);
                        BNode oldValue = null;
                        try {
                            oldValue = (BNode) f.get(this);
                        } catch (Exception ignored) {}

                        f.set(this, targetNode);

                        invalidateOutsCache();
                    } catch (
                            IllegalArgumentException
                            | IllegalAccessException e
                    ) {
                        throw new IllegalStateException(e);
                    }
                    return;
                }
            }
        }
    }

    public BVertex toVertex() {
        var v = new BVertex("" + id());
        v.label = toString();
        return v;
    }

    public static class InOutsNivoView
            extends NodeEndpoint<BNode>
            implements View {

        @Override
        public String whatItDoes() {
            return "generates a NIVO description of the graph";
        }

        public InOutsNivoView(BBGraph db) {
            super(db);
            endOfConstructor();
        }

        @Override
        public boolean sendContentByDefault() {
            return false;
        }

        @Override
        public EndpointResponse exec(
                ObjectNode in,
                User user,
                WebServer webServer,
                HttpsExchange exchange,
                BNode n
        ) {
            var g = new AnyGraph();
            var currentVertex = g.ensureHasVertex(n);
            setVertexProperties(currentVertex, n, "pink");
            currentVertex.size = 20;
            var limit = 99;
            AtomicInteger currentNumberNodes = new AtomicInteger(0);

            if (n.getClass() == BBGraph.class) {
                this.g.findAll(Cluster.class, c -> {
                    var clusterVertex = g.ensureHasVertex(c);
                    setVertexProperties(clusterVertex, c, "green");
                    var arc = g.newArc(currentVertex, clusterVertex);
                    arc.style = "dashed";
                    arc.label = c.prettyName();
                    return true;
                });
            } else {
                n.forEachOutField((role, outNode) -> {
                    if (
                            currentNumberNodes.get() <= limit ||
                                    outNode.getClass() == BBGraph.class
                    ) {
                        if (outNode.canSee(user) && n instanceof Cluster) {
                            var outVertex = g.ensureHasVertex(outNode);
                            setVertexProperties(outVertex, outNode, "blue");
                            var arc = g.newArc(currentVertex, outVertex);
                            arc.label = role;
                            arc.color = "red";
                            currentNumberNodes.getAndIncrement();
                        } else if (
                                outNode.canSee(user) &&
                                        !(outNode instanceof ValuedNode<?>)
                        ) {
                            var outVertex = g.ensureHasVertex(outNode);
                            setVertexProperties(outVertex, outNode, "blue");
                            var arc = g.newArc(currentVertex, outVertex);
                            arc.label = role;
                            arc.color = "red";
                            currentNumberNodes.getAndIncrement();
                        }
                    }
                });

                n.forEachIn((role, inNode) -> {
                    if (inNode.canSee(user) && n instanceof Cluster) {
                        var inVertex = g.ensureHasVertex(inNode);
                        setVertexProperties(inVertex, inNode, "pink");
                        var arc = g.newArc(inVertex, currentVertex);
                        arc.style = "dotted";
                        arc.label = role;
                    } else if (
                            inNode.canSee(user) &&
                                    !(inNode instanceof ValuedNode<?>)
                    ) {
                        var inVertex = g.ensureHasVertex(inNode);
                        setVertexProperties(inVertex, inNode, "pink");
                        var arc = g.newArc(inVertex, currentVertex);
                        arc.style = "dotted";
                        arc.label = role;
                    }
                });
            }

            return new EndpointJsonResponse(
                    g.toNivoJSON(),
                    dialects.nivoNetwork
            );
        }

        private void setVertexProperties(
                BVertex vertex,
                BNode node,
                String defaultColor
        ) {
            if (node.color == null || node.color.get() == null) {
                vertex.color = defaultColor;
            } else {
                vertex.color = node.color.getAsString();
            }
            vertex.label = node.prettyName();
            vertex.whatIsThis = node.whatIsThis();
            vertex.className = node.getClass().getName();
        }
    }

    public JsonNode toJSONNode() {
        var n = new ObjectNode(null);
        n.set("id", new IntNode(id()));
        n.set("pretty_name", new TextNode(prettyName()));
        n.set("color", new TextNode(Utilities.toRGBHex(getColor())));
        return n;
    }

    public Color getColor() {
        return Color.white;
    }

    public abstract String prettyName();

    public File directory() {
        Objects.requireNonNull(g.directory);
        var s = new StringBuilder();
        var ids = String.valueOf(id);

        for (int i = 0; i < ids.length(); i++) {
            s.append(ids.charAt(i));
            s.append('/');
        }

        return new File(g.directory, getClass().getName() + "/" + s);
    }

    public File outsFile() {
        return new File(directory(), "outs");
    }

    public boolean isPersisting(){
        return persisting;
    }

    public boolean isReadOnly(){
        return !isPersisting();
    }


    private void saveOuts() {
        if (!isPersisting())
            throw new IllegalStateException("can't save non-persisting node " + this);

        var s = new StringBuilder();

        forEachOutField((name, outNode) -> {
                    if (!(outNode instanceof BBGraph) && outNode.persisting) {
                        s.append(name+":" + outNode.id()).append("\n");
                    }
                }
        );


        try {
            var f = outsFile();
            g.logger.accept(BBGraph.LOGTYPE.FILE_WRITE, f.getAbsolutePath());
            Files.writeString(f.toPath(), s);
        } catch (IOException err) {
            throw new RuntimeException(err);
        }
    }

    public synchronized void save() throws IOException {
        if (!isPersisting()) throw new IllegalStateException(
                "can't save a non-persisting node " + getClass().getName()
        );

        var d = directory();

        if (!d.exists()) {
            g.logger.accept(BBGraph.LOGTYPE.FILE_READ, d.getAbsolutePath());
            d.mkdirs();
        }

        saveOuts();
    }

    // called by all node constructors
    protected void endOfConstructor(){
        constructionMonitor.countDown();
    }

    private int bnodeDepth(){
        int r = 0;

        for (Class<?> c = getClass(); c != BNode.class; c = c.getSuperclass()){
            r++;
        }

        return r;
    }

    public boolean isValid() {
        for (var c : Clazz.bfs(getClass())) {
            for (var f : c.getDeclaredFields()) {
                try {
                    f.setAccessible(true);
                    Object value = f.get(this);

                    if (
                            f.isAnnotationPresent(Required.class) &&
                                    (value == null ||
                                            (value instanceof ValuedNode &&
                                                    ((ValuedNode) value).get() == null))
                    ) {
                        return false;
                    }

                    if (value != null) {
                        if (f.isAnnotationPresent(Min.class)) {
                            Min min = f.getAnnotation(Min.class);
                            if (
                                    value instanceof ValuedNode &&
                                            ((ValuedNode<?>) value).get() instanceof Number
                            ) {
                                if (
                                        ((Number) ((ValuedNode<
                                                ?
                                                >) value).get()).doubleValue() <
                                                min.value()
                                ) {
                                    return false;
                                }
                            }
                        }

                        if (f.isAnnotationPresent(Max.class)) {
                            Max max = f.getAnnotation(Max.class);
                            if (
                                    value instanceof ValuedNode &&
                                            ((ValuedNode<?>) value).get() instanceof Number
                            ) {
                                if (
                                        ((Number) ((ValuedNode<
                                                ?
                                                >) value).get()).doubleValue() >
                                                max.value()
                                ) {
                                    return false;
                                }
                            }
                        }

                        if (f.isAnnotationPresent(Size.class)) {
                            Size size = f.getAnnotation(Size.class);
                            int length = -1;

                            if (
                                    value instanceof ValuedNode &&
                                            ((ValuedNode<?>) value).get() instanceof String
                            ) {
                                length = ((String) ((ValuedNode<
                                        ?
                                        >) value).get()).length();
                            } else if (value instanceof ListNode) {
                                length = ((ListNode<?>) value).size();
                            } else if (value instanceof ListNode) {
                                length = ((ListNode<?>) value).size();
                            }

                            if (
                                    length != -1 &&
                                            (length < size.min() || length > size.max())
                            ) {
                                return false;
                            }
                        }

                        if (f.isAnnotationPresent(Pattern.class)) {
                            Pattern pattern = f.getAnnotation(Pattern.class);
                            if (
                                    value instanceof ValuedNode &&
                                            ((ValuedNode<?>) value).get() instanceof String
                            ) {
                                if (
                                        !((String) ((ValuedNode<
                                                ?
                                                >) value).get()).matches(
                                                pattern.regex()
                                        )
                                ) {
                                    return false;
                                }
                            }
                        }
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return true;
    }

    /*
     * public static class BFS extends NodeEndpoint<BNode> {
     *
     * @Override public EndpointResponse exec(ObjectNode input, User user, WebServer
     * webServer, HttpsExchange exchange, ObjectNode r = null;
     *
     * List<BNode> q = new ArrayList<>(); BNode c = n; q.add(c); var visited = new
     * Int2ObjectOpenHashMap<ObjectNode>();
     *
     * while (!q.isEmpty()) { c = q.remove(0); var nn = visited.put(c.id(), new
     * ObjectNode(null)); r.add(nn);
     *
     * c.forEachOut((f, out) -> { if (!visited.containsKey(out)) { visited.add(new
     * ObjectNode(null)); q.add(out); } }); }
     *
     * var outs = new ObjectNode(null); n.forEachOut((name, o) -> outs.set(name, new
     * TextNode("" + o))); r.set("outs", outs); var ins = new ObjectNode(null);
     * n.forEachIn((name, o) -> ins.set(name, new TextNode("" + o))); r.set("ins",
     * ins); return r; }
     *
     * @Override public String whatIsThis() { return
     * "generates a JSON describing the local node and its out-nodes, up to a given depth"
     * ; }
     *
     * }
     */
}
