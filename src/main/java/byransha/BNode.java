package byransha;

import byransha.annotations.Max;
import byransha.annotations.Min;
import byransha.annotations.Pattern;
import byransha.annotations.Required;
import byransha.annotations.Size;
import byransha.graph.AnyGraph;
import byransha.graph.BVertex;
import byransha.web.*;
import byransha.web.EndpointJsonResponse.dialects;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sun.net.httpserver.HttpsExchange;
import java.awt.Color;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import toools.gui.Utilities;
import toools.reflect.Clazz;

public abstract class BNode {

    public String comment;
    public final BBGraph graph;
    private final int id;
    public ColorNode color;
    public Boolean isVisible = true;
    public Cluster cluster;

    protected BNode(BBGraph g) {
        this(g, g == null ? 0 : g.nextID());
    }

    protected BNode(BBGraph g, int id) {
        this.id = id;

        if (g != null) {
            this.graph = g;
            // g.accept(this);
        } else if (this instanceof BBGraph thisGraph) {
            this.graph = thisGraph;
        } else {
            throw new IllegalStateException();
        }
    }

    protected void initialized() {
        // This method can be overridden by subclasses to perform additional initialization
    }

    protected <N extends BNode> N searchFalseBoolean() {
        var node = graph.findAll(BooleanNode.class, n -> true);

        if (node == null) return null;

        for (var n : node) {
            if (n.get() == null) n.set(null, null, false);
            if (n.get().equals(false)) {
                return (N) n;
            }
        }

        return null;
    }

    public void createOrAssignCluster() {
        AtomicBoolean foundCluster = new AtomicBoolean(false);

        graph.findAll(Cluster.class, n -> {
            if (
                n.getTypeOfCluster() != null &&
                n.getTypeOfCluster().equals(this.getClass())
            ) {
                n.add(this);
                foundCluster.set(true);
                this.cluster = n;
                return true;
            }
            return false;
        });

        if (!foundCluster.get()) {
            var newCluster = graph.create(Cluster.class);
            newCluster.setTypeOfCluster(this.getClass());
            newCluster.add(this);
            newCluster.add(graph);
            cluster = newCluster;
            if (this instanceof Endpoint) newCluster.setColor("#00fff5");
        }
    }

    public void setColor(String newColor) {
        graph.findAll(ColorNode.class, n -> {
            if (n.getAsString().equals(newColor.toString())) {
                this.color = n;
                return true;
            }
            return false;
        });

        if (this.color == null || !this.color.getAsString().equals(newColor)) {
            this.color = graph.create(ColorNode.class);
            this.color.set(newColor);
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
        return graph.findRefsTO(this);
    }

    private static final ConcurrentMap<
        Class<?>,
        List<Field>
    > outNodeFieldsCache = new ConcurrentHashMap<>();

    public void forEachOutNodeField(Consumer<Field> consumer) {
        List<Field> fields = getOutNodeFields(getClass());
        for (Field f : fields) {
            try {
                consumer.accept(f);
            } catch (IllegalArgumentException err) {
                throw new IllegalStateException(err);
            }
        }
    }

    private static List<Field> getOutNodeFields(Class<?> clazz) {
        return outNodeFieldsCache.computeIfAbsent(clazz, cls -> {
            List<Field> fields = new ArrayList<>();
            for (var c : Clazz.bfs(cls)) {
                for (var f : c.getDeclaredFields()) {
                    if ((f.getModifiers() & Modifier.STATIC) != 0) continue;

                    if (BNode.class.isAssignableFrom(f.getType())) {
                        f.setAccessible(true);
                        fields.add(f);
                    }
                }
            }
            return Collections.unmodifiableList(fields);
        });
    }

    public void forEachOut(BiConsumer<String, BNode> consumer) {
        forEachOutNodeField(f -> {
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
            c.forEachOut((f, n) -> {
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
            forEachOut(outsCache::put);
            outsCacheDirty = false;
        }
        return new LinkedHashMap<>(outsCache);
    }

    protected void invalidateOutsCache() {
        outsCacheDirty = true;
    }

    public void setOut(String fieldName, BNode newTarget) {
        setField(fieldName, newTarget);
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
        return user.isAdmin();
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
        if (!(obj instanceof BNode)) return false;
        return this.hashCode() == obj.hashCode();
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
        BNode node = graph.findByID(id);
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

                        if (graph != null) {
                            graph.updateEdge(this, name, oldValue, targetNode);
                        }
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

    public static class BasicView extends NodeEndpoint<BNode> implements View {

        @Override
        public String whatItDoes() {
            return "";
        }

        public BasicView(BBGraph g) {
            super(g);
        }

        public BasicView(BBGraph g, int id) {
            super(g, id);
        }

        @Override
        public EndpointResponse exec(
            ObjectNode in,
            User u,
            WebServer webServer,
            HttpsExchange exchange,
            BNode node
        ) throws Throwable {
            var n = new ObjectNode(null);
            n.set("class", new TextNode(node.getClass().getName()));
            n.set("id", new TextNode("" + node.id()));
            n.set("comment", new TextNode(node.comment));

            if (node instanceof PersistingNode p) {
                var d = p.directory();

                if (d != null) {
                    n.set("directory", new TextNode(d.getAbsolutePath()));
                }
            }

            n.set("out-degree", new TextNode("" + node.outDegree()));
            n.set(
                "outs",
                new TextNode(
                    node
                        .outs()
                        .entrySet()
                        .stream()
                        .map(e -> e.getKey() + "=" + e.getValue())
                        .toList()
                        .toString()
                )
            );
            //			n.set("in-degree", new TextNode("" + node.ins().size()));
            //			n.set("ins", new TextNode(node.ins().stream().map(e -> e.toString()).toList().toString()));
            n.set("canSee", new TextNode("" + node.canSee(u)));
            n.set("canEdit", new TextNode("" + node.canEdit(u)));
            return new EndpointJsonResponse(n, this);
        }

        @Override
        public boolean sendContentByDefault() {
            return true;
        }
    }

    public static class Navigator extends NodeEndpoint<BNode> implements View {

        @Override
        public String whatItDoes() {
            return "navigates the graph";
        }

        public Navigator(BBGraph g) {
            super(g);
        }

        public Navigator(BBGraph g, int id) {
            super(g, id);
        }

        @Override
        public boolean sendContentByDefault() {
            return true;
        }

        @Override
        public EndpointResponse exec(
            ObjectNode in,
            User u,
            WebServer webServer,
            HttpsExchange exchange,
            BNode n
        ) {
            var r = new ObjectNode(null);
            var outs = new ObjectNode(null);
            n.forEachOut((name, o) ->
                outs.set(name, new TextNode("" + o.id()))
            );
            r.set("outs", outs);
            var ins = new ObjectNode(null);
            n.forEachIn((name, o) -> ins.set(name, new TextNode("" + o.id())));
            r.set("ins", ins);
            return new EndpointJsonResponse(r, "bnode_nav2");
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
        }

        public InOutsNivoView(BBGraph db, int id) {
            super(db, id);
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

            if (n.getClass().getSimpleName().equals("BBGraph")) {
                graph.findAll(Cluster.class, c -> {
                    var clusterVertex = g.ensureHasVertex(c);
                    setVertexProperties(clusterVertex, c, "green");
                    var arc = g.newArc(currentVertex, clusterVertex);
                    arc.style = "dashed";
                    arc.label = c.prettyName();
                    return true;
                });
            } else {
                n.forEachOut((role, outNode) -> {
                    if (
                        currentNumberNodes.get() <= limit ||
                        outNode.getClass().getSimpleName().equals("BBGraph")
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

    public static class OutDegreeDistribution
        extends NodeEndpoint<BNode>
        implements View {

        @Override
        public String whatItDoes() {
            return "shows distribution for out nodes";
        }

        public OutDegreeDistribution(BBGraph db) {
            super(db);
        }

        public OutDegreeDistribution(BBGraph db, int id) {
            super(db, id);
        }

        @Override
        public EndpointResponse exec(
            ObjectNode in,
            User user,
            WebServer webServer,
            HttpsExchange exchange,
            BNode node
        ) throws Throwable {
            var d = new Byransha.Distribution<Integer>();
            BBGraph g = (node instanceof BBGraph) ? (BBGraph) node : node.graph;
            g.forEachNode(n -> d.addOccurence(n.outDegree()));
            return new EndpointJsonResponse(d.toJson(), dialects.distribution);
        }

        @Override
        public boolean sendContentByDefault() {
            return false;
        }
    }

    public static class ClassDistribution
        extends NodeEndpoint<BNode>
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
            BNode node
        ) throws Throwable {
            var d = new Byransha.Distribution<String>();
            BBGraph g = (node instanceof BBGraph) ? (BBGraph) node : node.graph;
            g.forEachNode(n -> d.addOccurence(n.getClass().getName()));
            return new EndpointJsonResponse(d.toJson(), dialects.distribution);
        }

        @Override
        public boolean sendContentByDefault() {
            return false;
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
