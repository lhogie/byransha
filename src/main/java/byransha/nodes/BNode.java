package byransha.nodes;

import byransha.BBGraph;
import byransha.NodeAction;
import byransha.SearchResult;
import byransha.annotations.*;
import byransha.nodes.primitive.ListNode;
import byransha.nodes.primitive.ValuedNode;
import byransha.nodes.system.User;
import byransha.web.*;
import byransha.web.EndpointJsonResponse.dialects;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sun.net.httpserver.HttpsExchange;
import graph.AnyGraph;
import graph.BVertex;
import org.apache.commons.lang3.tuple.Pair;
import toools.gui.Utilities;
import toools.reflect.Clazz;

import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class BNode {
    public BBGraph g;
    public boolean readOnly;
    private int id;
    private LinkedHashMap<String, BNode> outsCache;
    private boolean outsCacheDirty = true;

    protected BNode(BBGraph g, User creator) {
        if (!canCreate(creator))
            throw new IllegalStateException("can't create " + creator);

        if (g == null) {
            this.id = 0;
            this.g = (BBGraph) this;
        } else {
            this.g = g;

            if (this instanceof NodeEndpoint ne) {
                var alreadyInName = this.g.findEndpoint(ne.name());

                if (alreadyInName != null) {
                    throw new IllegalArgumentException("adding " + ne + ", endpoint with same name '" + ne.name() + "' already there: " + alreadyInName.getClass().getName());
                }
            }
        }
    }

    public final NodeAction reset = new NodeAction() {

        @Override
        public String description() {
            return "reset the values";
        }

        @Override
        public BNode exec(User user) {
            forEachOutField(f -> {
                try {
                    var v = (BNode) f.get(BNode.this);

                    if (v instanceof ValuedNode vn) {
                        vn.set(vn.defaultValue(), user);
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            });
            return null;
        }
    };

    public final NodeAction delete = new NodeAction() {

        @Override
        public String description() {
            return "delete from the graph";
        }

        @Override
        public BNode exec(User user) {
            forEachIn((role, parent) -> {
                parent.removeOut(BNode.this);
            });
            g.nodesById.remove(id());
            return g;
        }
    };

    public void removeOut(BNode out, User user) {
        forEachOutField(f -> {
            try {
                if (f.get(this) == out){
                    f.set(this, null);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public  void forEachOutField( Consumer<Field> consumer) {
, boolean bNodeReached = false;
        for (Class c = getClass(); !bNodeReached; c = c.getSuperclass())
        {
            bNodeReached = c == BNode.class;

            for (var f : c.getDeclaredFields()) {
                if ((f.getModifiers() & Modifier.STATIC) != 0) continue;
                f.setAccessible(true);

                if (BNode.class.isAssignableFrom(f.getType())) {
                    consumer.accept(f);
                }
            }
        }
    }


    public void forEachOut(BiConsumer<String, BNode> consumer) {
        forEachOutField(this, f -> {
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

    public void setID(int newID) {
        BNode previous = this.g.nodesById.putIfAbsent(this.id(), this);

        if (previous != null && previous != this)
            throw new IllegalStateException("can't add node " + this + " because its ID " + this.id() + " is already taken by: " + previous);

        this.id = newID;
    }

    public abstract String whatIsThis();

    public List<InLink> ins() {
        return g.findRefsTO(this);
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

    private void search(Consumer<BNode> consumer, Function<List<BNode>, BNode> listExtractor) {
        List<BNode> q = new ArrayList<>();
        BNode c = this;
        q.add(c);
        var visited = new HashSet<BNode>();

        while (!q.isEmpty()) {
            c = listExtractor.apply(q);
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

    protected void onEdgeChanged(String fieldName, BNode oldTarget, BNode newTarget) {
    }

    public void remove() {
        invalidateOutsCache();
    }

    public List<SearchResult> search(String query, User user) {
        var r = new ArrayList<SearchResult>();
        bfs(n -> r.add(new SearchResult(query, n, n.distanceToSearchString(query, user))));
        Collections.sort(r);
        return r;
    }

    public int distanceToSearchString(String s, User user) {
        return 1;
    }

    public boolean canSee(User user) {
        return true;
    }

    public boolean canEdit(User user) {
        return !isReadOnly();
    }


    public boolean canCreate(User user) {
        return true;
    }

    public boolean matches(NodeEndpoint v) {
        return v.getTargetNodeType().isAssignableFrom(getClass());
    }

    @Override
    public String toString() {
        return prettyName();
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
                        } catch (Exception ignored) {
                        }

                        f.set(this, targetNode);

                        invalidateOutsCache();
                    } catch (IllegalArgumentException | IllegalAccessException e) {
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

    public JsonNode toJSONNode() {
        var n = new ObjectNode(null);
        n.set("id", new IntNode(id()));
        n.set("pretty_name", new TextNode(prettyName()));
        n.set("color", new TextNode(Utilities.toRGBHex(getColor())));
        return n;
    }

    public Color getColor() {
        return g.getColorForNodeClass(getClass());
    }

    public abstract String prettyName();

    public boolean isReadOnly() {
        return readOnly;
    }


    private int bnodeDepth() {
        int r = 0;

        for (Class<?> c = getClass(); c != BNode.class; c = c.getSuperclass()) {
            r++;
        }

        return r;
    }

    public Pair<BNode, Field> getParentNodeWithField() {
        for (InLink inLink : ins()) {
            BNode sourceNode = inLink.source();

            // Check fields in the entire class hierarchy
            Class<?> currentClass = sourceNode.getClass();
            while (currentClass != null && currentClass != Object.class) {
                for (Field field : currentClass.getDeclaredFields()) {
                    if ((field.getModifiers() & Modifier.STATIC) != 0) continue;
                    field.setAccessible(true);

                    try {
                        if (field.get(sourceNode) == this) {
                            Pair<BNode, Field> result = Pair.of(sourceNode, field);
                            return result;
                        }
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Error accessing field: " + e.getMessage(), e);
                    }
                }
                // Move to the parent class
                currentClass = currentClass.getSuperclass();
            }
        }

        return null;
    }

    public boolean isValid() {
        for (var c : Clazz.bfs(getClass())) {
            for (var f : c.getDeclaredFields()) {
                try {
                    f.setAccessible(true);
                    Object value = f.get(this);

                    if (f.isAnnotationPresent(Required.class) && (value == null || (value instanceof ValuedNode && ((ValuedNode) value).get() == null))) {
                        return false;
                    }

                    if (value != null) {
                        if (f.isAnnotationPresent(Min.class)) {
                            Min min = f.getAnnotation(Min.class);
                            if (value instanceof ValuedNode && ((ValuedNode<?>) value).get() instanceof Number) {
                                if (((Number) ((ValuedNode<?>) value).get()).doubleValue() < min.value()) {
                                    return false;
                                }
                            }
                        }

                        if (f.isAnnotationPresent(Max.class)) {
                            Max max = f.getAnnotation(Max.class);
                            if (value instanceof ValuedNode && ((ValuedNode<?>) value).get() instanceof Number) {
                                if (((Number) ((ValuedNode<?>) value).get()).doubleValue() > max.value()) {
                                    return false;
                                }
                            }
                        }

                        if (f.isAnnotationPresent(Size.class)) {
                            Size size = f.getAnnotation(Size.class);
                            int length = -1;

                            if (value instanceof ValuedNode && ((ValuedNode<?>) value).get() instanceof String) {
                                length = ((String) ((ValuedNode<?>) value).get()).length();
                            } else if (value instanceof ListNode) {
                                length = ((ListNode<?>) value).size();
                            } else if (value instanceof ListNode) {
                                length = ((ListNode<?>) value).size();
                            }

                            if (length != -1 && (length < size.min() || length > size.max())) {
                                return false;
                            }
                        }

                        if (f.isAnnotationPresent(Pattern.class)) {
                            Pattern pattern = f.getAnnotation(Pattern.class);
                            if (value instanceof ValuedNode && ((ValuedNode<?>) value).get() instanceof String) {
                                if (!((String) ((ValuedNode<?>) value).get()).matches(pattern.regex())) {
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


    public record InLink(String role, BNode source) {
        @Override
        public String toString() {
            return source + "." + role;
        }
    }

    public static class InOutsNivoView extends NodeEndpoint<BNode> implements TechnicalView {

        public InOutsNivoView(BBGraph db) {
            super(db);
        }

        @Override
        public String whatItDoes() {
            return "generates a NIVO description of the graph";
        }

        @Override
        public boolean sendContentByDefault() {
            return false;
        }

        @Override
        public EndpointResponse exec(ObjectNode in, User user, WebServer webServer, HttpsExchange exchange, BNode n) {
            var g = new AnyGraph();
            var currentVertex = g.ensureHasVertex(n);
            setVertexProperties(currentVertex, n, "pink");
            currentVertex.size = 20;
            var limit = 99;
            AtomicInteger currentNumberNodes = new AtomicInteger(0);

            if (n.getClass() == BBGraph.class) {
            } else {
                n.forEachOutField((role, outNode) -> {
                    if (currentNumberNodes.get() <= limit || outNode.getClass() == BBGraph.class) {
                        if (outNode.canSee(user) && n instanceof Cluster) {
                            var outVertex = g.ensureHasVertex(outNode);
                            setVertexProperties(outVertex, outNode, "blue");
                            var arc = g.newArc(currentVertex, outVertex);
                            arc.label = role;
                            arc.color = "red";
                            currentNumberNodes.getAndIncrement();
                        } else if (outNode.canSee(user) && !(outNode instanceof ValuedNode<?>)) {
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
                     if (inNode.canSee(user) && !(inNode instanceof ValuedNode<?>)) {
                        var inVertex = g.ensureHasVertex(inNode);
                        setVertexProperties(inVertex, inNode, "pink");
                        var arc = g.newArc(inVertex, currentVertex);
                        arc.style = "dotted";
                        arc.label = role;
                    }
                });
            }

            return new EndpointJsonResponse(g.toNivoJSON(), dialects.nivoNetwork);
        }

        private void setVertexProperties(BVertex vertex, BNode node, String defaultColor) {
            vertex.color = node.getColor().getAsString();
            vertex.label = node.prettyName();
            vertex.whatIsThis = node.whatIsThis();
            vertex.className = node.getClass().getName();
        }
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
