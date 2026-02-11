package byransha;

import butils.Utils;
import byransha.nodes.BNode;
import byransha.nodes.primitive.BooleanNode;
import byransha.nodes.system.*;
import graph.AnyGraph;
import graph.BVertex;
import byransha.web.*;
import byransha.web.EndpointJsonResponse.dialects;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpsExchange;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.*;

import it.unimi.dsi.fastutil.objects.Object2BooleanFunction;
import toools.Stop;
import toools.reflect.Clazz;

public class BBGraph extends BNode {
    public ConcurrentMap<Integer, BNode> nodesById  = new ConcurrentHashMap<>();
    private final AtomicInteger idSequence = new AtomicInteger(1);

    private final User admin, system;
    public UserApplication application;
    private final WebServer webServer;

    public BBGraph( Map<String, String> argMap)
            throws Exception {
        super(null, null);

        var appClass = (Class<? extends UserApplication>) Class.forName(argMap.remove("appClass"));

        nodesById.put(0, this);

        this.application = appClass.getConstructor(BBGraph.class, User.class).newInstance(this, admin());
        int port = Integer.parseInt(argMap.getOrDefault("-port", "8080"));
        this.webServer = new WebServer(this, port);

        new JVMNode(g);
        new Byransha(g, systemUser());
        new OSNode(g);
        this.admin = new User(this, null, "admin", "admin"); // self accept
        this.system = new User(this, null, "system", ""); // self accept
        new User(g, systemUser(), "user", "test");
        new SearchForm(g, systemUser());
}



    public Color getColorForNodeClass(Class<? extends BNode> aClass) {
        return Color.white;
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

        forEachNode(node -> {
            node.forEachOut((role, target) -> {
                if (target != null && target == searchedNode) {
                    refs.add(new InLink(role, node));
                }
            });
        });

        return refs;
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
        nodesById.values().forEach(h);
    }

    public long countNodes() {
        return nodesById.size();
    }

    public BNode root() {
        return this;
    }


    public BNode findByID(int id) {
        return nodesById.get(id);
    }


    public void forEachNode(Function<BNode, Stop> f) {
        for (BNode node : nodesById.values()) {
            if (f.apply(node) == Stop.yes){
                break;
            }
        }
    }


    public <C extends BNode> void forEachNodeOfClass(Class<C> nodeClass, Function<C, Stop> f) {
        forEachNode(n -> {
            if (nodeClass.isAssignableFrom(n.getClass())) {
                 return f.apply((C) n);
            }else{
                return Stop.no;
            }
        });
    }

    public <C extends BNode> void forEachNode(Class<C> nodeClass, Predicate<C> p, Function<C, Stop> f) {
        forEachNodeOfClass(nodeClass, n -> {
            if (p.test(n)) {
                c.accept(n);
            }
        });
    }


    public List<User> users() {
        return nodesById
                .values()
                .stream()
                .filter(n -> n instanceof User)
                .map(n -> (User) n)
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
