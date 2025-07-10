package byransha.web;

import byransha.*;
import byransha.BBGraph;
import byransha.BNode;
import byransha.Byransha;
import byransha.JVMNode;
import byransha.ListNode;
import byransha.ListNode.ListNodes;
import byransha.Log;
import byransha.OSNode;
import byransha.StringNode;
import byransha.UI;
import byransha.User;
import byransha.User.History;
import byransha.graph.AnyGraph;
import byransha.labmodel.I3S;
import byransha.labmodel.model.v0.*;
import byransha.labmodel.model.v0.view.LabView;
import byransha.labmodel.model.v0.view.StructureView;
import byransha.web.endpoint.*;
import byransha.web.view.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsExchange;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import toools.reflect.ClassPath;
import toools.text.TextUtilities;

/**
 * https://syncagio.medium.com/how-to-setup-java-httpsserver-and-keystore-eb74a8bd89d
 */

public class WebServer extends BNode {

    public static final File defaultDBDirectory = new File(
        System.getProperty("user.home") + "/." + BBGraph.class.getPackageName()
    );

    public static void main(String[] args) throws Exception {
        var argList = List.of(args);
        var argMap = new HashMap<String, String>();

        // version sur disque
        // argMap.put("--createDB", "true");

        argList
            .stream()
            .map(a -> a.split("="))
            .forEach(a -> argMap.put(a[0], a[1]));
        BBGraph g = instantiateGraph(argMap);
        int port = Integer.parseInt(argMap.getOrDefault("-port", "8080"));
        new WebServer(g, port);
    }

    public static BBGraph instantiateGraph(Map<String, String> argMap)
        throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException, IOException {
        if (defaultDBDirectory.exists()) {
            // ligne pour version serveur
            var g = (BBGraph) Class.forName(
                Files.readString(
                    new File(defaultDBDirectory, "dbClass.txt").toPath()
                )
            )
                .getConstructor(File.class)
                .newInstance(defaultDBDirectory);
            System.out.println("loading DB from " + defaultDBDirectory);

            // ligne ajouter pour sur disque
            // var g = new BBGraph(defaultDBDirectory);

            g.loadFromDisk(
                n -> System.out.println("loading node " + n),
                (n, s) -> System.out.println("loading arc " + n + ", " + s)
            );
            return g;
        } else if (argMap.containsKey("--createDB")) {
            return new BBGraph(defaultDBDirectory);
        } else {
            var g = new BBGraph(null);
            var p = new Person(g);
            p.etatCivil = new EtatCivil(g);
            p.etatCivil.nomUsuel = new StringNode(g, "Caro");
            p.etatCivil.prenom = new StringNode(g, "George");
            //p.etatCivil.nationalites = new StringNode(g, "FR");
            var lab = new I3S(g);
            lab.members.add(p);
            lab.director = p;
            return g;
        }
    }

    static final ObjectMapper mapper = new ObjectMapper();

    final List<User> nbRequestsInProgress = Collections.synchronizedList(
        new ArrayList<>()
    );

    private final HttpsServer httpsServer;
    public final List<Log> logs = new CopyOnWriteArrayList<>();

    private final SessionStore sessionStore;

    public WebServer(BBGraph g, int port) throws Exception {
        super(g);
        this.sessionStore = new SessionStore();
        createSpecialNodes(g);
        createEndpoints(g);
        createDemoNodes(g);

        //var dot = ModelDOTView.toDot(g, c -> !Endpoint.class.isAssignableFrom(c));
        //var svg = ModelGraphivzSVGView.gen(dot, "fdp");
        //var f = Path.of("/Users/lhogie/a/job/byransha/model.svg");
        //System.out.print("writing " + f);
        //Files.write(f, svg);
        //System.out.println("   done");

        try {
            Path classPathFile = new File(
                Byransha.class.getPackageName() + "-classpath.lst"
            ).toPath();
            System.out.println("writing " + classPathFile);
            Files.write(
                classPathFile,
                ClassPath.retrieveSystemClassPath().toString().getBytes()
            );
        } catch (IOException err) {
            err.printStackTrace();
        }

        System.out.println("starting HTTP server on port " + port);

        int backlog = 100;
        httpsServer = HttpsServer.create(new InetSocketAddress(port), backlog);
        httpsServer.setHttpsConfigurator(
            new HttpsConfigurator(getSslContext()) {
                @Override
                public void configure(HttpsParameters params) {
                    try {
                        SSLContext context = getSSLContext();
                        params.setNeedClientAuth(false);

                        context.getClientSessionContext().setSessionTimeout(60);

                        String[] enabledProtocols = { "TLSv1.3", "TLSv1.2" };
                        params.setProtocols(enabledProtocols);

                        SSLParameters defaultSSLParameters =
                            context.getDefaultSSLParameters();
                        List<String> strongCipherSuites = new ArrayList<>(
                            List.of(defaultSSLParameters.getCipherSuites())
                        );
                        params.setCipherSuites(
                            strongCipherSuites.toArray(new String[0])
                        );
                        params.setSSLParameters(
                            context.getSupportedSSLParameters()
                        );
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        );

        httpsServer.createContext("/", http ->
            processRequest((HttpsExchange) http).send(http)
        );
        httpsServer.setExecutor(Executors.newCachedThreadPool());
        httpsServer.start();
    }

    private void createDemoNodes(BBGraph g) {
        var l = new ListNode<BNode>(g);
        l.add(new StringNode(g, "a"));
        l.add(new StringNode(g, "b"));
        l.add(new StringNode(g, "c"));

        System.out.println(g.classes().contains(Building.class));
        var b = new Building(g);
        System.out.println(g.classes().contains(Building.class));

        List<Person> persons = new ArrayList<>();

        for (int i = 0; i < 10; ++i) {
            var p = new Person(g);
            persons.add(p);
            p.etatCivil = new EtatCivil(g);
            p.etatCivil.adressePersonnelle.set("2000, route des Lucioles");
        }

        var sophiaTech = new Campus(g);
        sophiaTech.buildings.add(b);
        sophiaTech.name.set("Sophiatech");

        for (int i = 0; i < 5; ++i) {
            var o = new Office(g);
            o.name.set("office" + i);
            o.users.add(persons.get(i));
            o.users.add(persons.get(i + 5));
            b.offices.add(o);
        }

        var cnrs = new CNRS(g);
        persons.forEach(p -> cnrs.members.add(p));
        var publication = new Publication(g);
        publication.title.set("156 maitre du temps - MAIN-1");
        publication.acmClassifier = new ACMClassifier(
            g,
            "B.1.4",
            "Microprogram Design Aids"
        );
    }

    private void createSpecialNodes(BBGraph g) {
        BNode.create(g, JVMNode.class);
        BNode.create(g, Byransha.class);
        BNode.create(g, OSNode.class);
        var user1 = BNode.create(g, User.class);
        var user2 = BNode.create(g, User.class);
        var user3 = BNode.create(g, User.class);

        user1.name.set("user");
        user1.passwordNode.set("test");
        user2.name.set("toto");
        user2.passwordNode.set("toto");
        user3.name.set("admin");
        user3.passwordNode.set("admin");
    }

    private void createEndpoints(BBGraph g) {
        BNode.create(g, NodeInfo.class);
        BNode.create(g, Views.class);
        BNode.create(g, Jump.class);
        BNode.create(g, Endpoints.class);
        BNode.create(g, JVMNode.Kill.class);
        var n = BNode.create(g, Authenticate.class);
        n.setSessionStore(sessionStore);
        var l = BNode.create(g, Logout.class);
        l.setSessionStore(sessionStore);
        BNode.create(g, Nodes.class);
        BNode.create(g, EndpointCallDistributionView.class);
        BNode.create(g, Info.class);
        BNode.create(g, Logs.class);
        BNode.create(g, BasicView.class);
        BNode.create(g, CharExampleXY.class);
        BNode.create(g, User.UserView.class);
        BNode.create(g, BBGraph.GraphNivoView.class);
        BNode.create(g, OSNode.View.class);
        BNode.create(g, JVMNode.View.class);
        BNode.create(g, BNode.InOutsNivoView.class);
        BNode.create(g, ModelGraphivzSVGView.class);
        BNode.create(g, Navigator.class);
        BNode.create(g, OutDegreeDistribution.class);
        BNode.create(g, ClassDistribution.class);
        BNode.create(g, Picture.V.class);
        BNode.create(g, LabView.class);
        BNode.create(g, ModelDOTView.class);
        BNode.create(g, ToStringView.class);
        BNode.create(g, StructureView.class);
        BNode.create(g, NodeEndpoints.class);
        BNode.create(g, SetValue.class);
        BNode.create(g, AnyGraph.Classes.class);
        BNode.create(g, Edit.class);
        BNode.create(g, History.class);
        BNode.create(g, UI.class);
        BNode.create(g, UI.getProperties.class);
        BNode.create(g, Summarizer.class);
        BNode.create(g, LoadImage.class);
        BNode.create(g, ListNodes.class);
        BNode.create(g, ClassInformation.class);
        BNode.create(g, ClassAttributeField.class);
        BNode.create(g, AddNode.class);
        BNode.create(g, AddExistingNode.class);
        BNode.create(g, ListExistingNode.class);
        BNode.create(g, SearchNode.class);
        BNode.create(g, Agent.class);
        BNode.create(g, AgentCdd.class);
        BNode.create(g, ExportCSV.class);
        BNode.create(g, RemoveFromList.class);
        BNode.create(g, RemoveNode.class);
        BNode.create(g, ColorNodeView.class);
        BNode.create(g, SearchForm.class);

        Country.loadCountries(g);
    }

    public SessionStore getSessionStore() {
        return sessionStore;
    }

    @Override
    public String whatIsThis() {
        return "serves HTTP requests from the frontend";
    }

    @Override
    public void forEachOut(BiConsumer<String, BNode> consumer) {
        super.forEachOut(consumer);
        consumer.accept("active users", activeUsers());
    }

    public ListNode<User> activeUsers() {
        ListNode<User> activeUsers = new ListNode<>(graph);

        graph.forEachNode(n -> {
            if (n instanceof User u) {
                activeUsers.add(u);
                System.out.println("active user: " + u.name.get());
            }
        });

        return activeUsers;
    }

    static final File frontendDir = new File("build/frontend");

    private static final Map<String, CachedFile> fileCache =
        new ConcurrentHashMap<>();
    private static final long MAX_CACHE_SIZE = 50 * 1024 * 1024;
    private static long currentCacheSize = 0;

    private static class CachedFile {

        final byte[] content;
        final String contentType;
        final long lastModified;
        final String eTag;
        long lastAccessed;

        CachedFile(byte[] content, String contentType, long lastModified) {
            this.content = content;
            this.contentType = contentType;
            this.lastModified = lastModified;
            this.eTag =
                "\"" +
                Integer.toHexString(java.util.Arrays.hashCode(content)) +
                "\"";
            this.lastAccessed = System.currentTimeMillis();
        }

        long size() {
            return content.length;
        }

        void updateLastAccessed() {
            this.lastAccessed = System.currentTimeMillis();
        }
    }

    /**
     * Adds a file to the cache, evicting least recently used files if necessary.
     *
     * @param key The cache key (usually the file path)
     * @param content The file content
     * @param contentType The content type of the file
     * @param lastModified The last modified timestamp of the file
     */
    private static synchronized void addToCache(
        String key,
        byte[] content,
        String contentType,
        long lastModified
    ) {
        if (content.length > 5 * 1024 * 1024) {
            return;
        }

        CachedFile existing = fileCache.remove(key);
        if (existing != null) {
            currentCacheSize -= existing.size();
        }

        CachedFile newEntry = new CachedFile(
            content,
            contentType,
            lastModified
        );

        while (
            currentCacheSize + newEntry.size() > MAX_CACHE_SIZE &&
            !fileCache.isEmpty()
        ) {
            String lruKey = null;
            long oldestAccess = Long.MAX_VALUE;

            for (Map.Entry<String, CachedFile> entry : fileCache.entrySet()) {
                if (entry.getValue().lastAccessed < oldestAccess) {
                    oldestAccess = entry.getValue().lastAccessed;
                    lruKey = entry.getKey();
                }
            }

            if (lruKey != null) {
                CachedFile evicted = fileCache.remove(lruKey);
                if (evicted != null) {
                    currentCacheSize -= evicted.size();
                }
            }
        }

        fileCache.put(key, newEntry);
        currentCacheSize += newEntry.size();
    }

    private HTTPResponse processRequest(HttpsExchange https) {
        User user = null;
        SessionStore.SessionData sessionData;
        long startTimeNs = System.nanoTime();
        ObjectNode inputJson;

        try {
            inputJson = grabInputFromURLandPOST(https);
            final var inputJson2sendBack = (inputJson != null)
                ? inputJson.deepCopy()
                : new ObjectNode(null);

            String sessionToken = null;
            String cookieHeader = https.getRequestHeaders().getFirst("Cookie");

            if (cookieHeader != null) {
                for (String cookie : cookieHeader.split(";")) {
                    cookie = cookie.trim();
                    if (cookie.startsWith("session_token=")) {
                        sessionToken = cookie.substring(
                            "session_token=".length()
                        );
                        break;
                    }
                }
            }

            if (sessionToken != null) {
                Optional<SessionStore.SessionData> sessionOpt =
                    sessionStore.getValidSession(sessionToken);
                if (sessionOpt.isPresent()) {
                    sessionData = sessionOpt.get();
                    user = (User) graph.findByID(sessionData.userId());
                    if (user == null) {
                        System.err.println(
                            "User ID " +
                            sessionData.userId() +
                            " from session token " +
                            sessionToken.substring(0, 8) +
                            "... not found in graph. Invalidating session."
                        );
                        sessionStore.removeSession(sessionToken);
                        Authenticate.deleteSessionCookie(
                            https,
                            "session_token"
                        );
                        sessionData = null;
                    }
                } else {
                    Authenticate.deleteSessionCookie(https, "session_token");
                }
            }

            if (user == null) {
                user = graph.find(User.class, u ->
                    u.name.get().equals("guest")
                );
                if (user == null) {
                    user = BNode.create(graph, User.class);
                    user.name.set("guest");
                    user.passwordNode.set("guest");
                    // user.stack.push(graph.root());
                }
            }

            var path = https.getRequestURI().getPath();

            if (!path.endsWith("/")) {
                path += "/";
            }

            if (path.startsWith("/api/")) {
                nbRequestsInProgress.add(user);

                String endpointName = path.substring(5);
                if (endpointName.endsWith("/")) {
                    endpointName = endpointName.substring(
                        0,
                        endpointName.length() - 1
                    );
                }

                List<NodeEndpoint> resolvedEndpoints;
                BNode contextNode = user.currentNode() != null
                    ? user.currentNode()
                    : graph.root();

                if (endpointName.isEmpty()) {
                    User finalUser = user;
                    resolvedEndpoints = graph
                        .endpointsUsableFrom(contextNode)
                        .stream()
                        .filter(e -> e instanceof View)
                        .filter(e -> e.canSee(finalUser))
                        .toList();
                } else {
                    var specificEndpoint = graph.findEndpoint(endpointName);
                    if (specificEndpoint == null) {
                        throw new IllegalArgumentException(
                            "No such endpoint: " + endpointName
                        );
                    }
                    resolvedEndpoints = List.of(specificEndpoint);
                }

                var response = new ObjectNode(null);
                response.set("backend version", new TextNode(Byransha.VERSION));
                long uptimeMs =
                    ManagementFactory.getRuntimeMXBean().getUptime();
                response.set(
                    "uptimeMs",
                    new TextNode(Duration.ofMillis(uptimeMs).toString())
                );
                if (!inputJson2sendBack.isEmpty()) response.set(
                    "request",
                    inputJson2sendBack
                );

                response.set("username", new TextNode(user.name.get()));
                response.set("user_id", new IntNode(user.id()));
                response.set(
                    "node_id",
                    new TextNode(
                        user.currentNode() == null
                            ? "N/A"
                            : "" + user.currentNode().id()
                    )
                );

                var resultsNode = new ArrayNode(null);
                response.set("results", resultsNode);

                assert inputJson != null;
                boolean rawRequest = inputJson.remove("raw") != null;

                if (rawRequest && resolvedEndpoints.size() != 1) {
                    throw new IllegalArgumentException(
                        "Raw request requires exactly one endpoint, found: " +
                        resolvedEndpoints.size()
                    );
                }

                boolean hasErrors = false;
                int responseStatusCode = 200;

                for (var endpoint : resolvedEndpoints) {
                    ObjectNode er = new ObjectNode(null);
                    er.set("endpoint", new TextNode(endpoint.name()));
                    er.set(
                        "endpoint_class",
                        new TextNode(endpoint.getClass().getName())
                    );
                    er.set(
                        "response_type",
                        new TextNode(endpoint.type().name())
                    );
                    er.set("pretty_name", new TextNode(endpoint.prettyName()));
                    er.set("what_is_this", new TextNode(endpoint.whatIsThis()));
                    long startTimeNs2 = System.nanoTime();

                    try {
                        // TODO: Add back for security
                        boolean isGuestUser = user.name.get().equals("guest");

                        /*
                         * if (endpoint.requiresAuthentication() && isGuestUser) { throw new
                         * SecurityException("Authentication required for endpoint: " +
                         * endpoint.name()); }
                         */

                        if (!endpoint.canExec(user)) {
                            throw new SecurityException(
                                "User '" +
                                user.name.get() +
                                "' is not authorized to execute endpoint: " +
                                endpoint.name()
                            );
                        }

                        EndpointResponse result = endpoint.exec(
                            inputJson,
                            user,
                            this,
                            https
                        );

                        if (rawRequest) {
                            if (!inputJson.isEmpty()) System.err.println(
                                "Warning: Parameters potentially unused in raw request: " +
                                inputJson.toPrettyString()
                            ); // Log warning
                            return new HTTPResponse(
                                result.getStatusCode(),
                                result.contentType,
                                result.toRawText().getBytes()
                            );
                        } else {
                            er.set("result", result.toJson());

                            if (
                                result.getStatusCode() != 200 &&
                                responseStatusCode == 200
                            ) {
                                responseStatusCode = result.getStatusCode();
                            }
                        }
                    } catch (SecurityException authEx) {
                        hasErrors = true;
                        boolean isSpecificRequest = !endpointName.isEmpty();
                        int statusCode = authEx
                                .getMessage()
                                .startsWith("Authentication required")
                            ? 401
                            : 403;

                        if (rawRequest || isSpecificRequest) {
                            return new HTTPResponse(
                                statusCode,
                                "text/plain",
                                authEx.getMessage().getBytes()
                            );
                        } else {
                            er.set("error", new TextNode(authEx.getMessage()));
                            er.set(
                                "error_type",
                                new TextNode(
                                    authEx
                                            .getMessage()
                                            .startsWith(
                                                "Authentication required"
                                            )
                                        ? "AuthenticationError"
                                        : "AuthorizationError"
                                )
                            );

                            if (responseStatusCode == 200) {
                                responseStatusCode = statusCode;
                            }
                        }
                    } catch (Throwable err) {
                        hasErrors = true;
                        err.printStackTrace();
                        var sw = new StringWriter();
                        err.printStackTrace(new PrintWriter(sw));
                        String errorMsg = sw.toString();

                        if (rawRequest) {
                            return new HTTPResponse(
                                500,
                                "text/plain",
                                ("Endpoint execution failed: " +
                                    err.getMessage()).getBytes()
                            );
                        } else {
                            er.set("error", new TextNode(errorMsg));
                            er.set(
                                "error_type",
                                new TextNode("ExecutionError")
                            );

                            if (responseStatusCode == 200) {
                                responseStatusCode = 500;
                            }
                        }
                    }

                    long durationNs = System.nanoTime() - startTimeNs2;
                    endpoint.nbCalls.incrementAndGet();
                    endpoint.timeSpentNs.addAndGet(durationNs);
                    er.set("durationNs", new DoubleNode(durationNs));

                    resultsNode.add(er);
                }

                if (!inputJson.isEmpty()) {
                    System.err.println(
                        "Warning: Parameters unused after processing all endpoints: " +
                        inputJson.toPrettyString()
                    );
                    response.set(
                        "unused_parameters_warning",
                        new TextNode(
                            "Some request parameters were not used by any executed endpoint: " +
                            inputJson.toPrettyString()
                        )
                    );
                }

                response.set(
                    "durationNs",
                    new TextNode("" + (System.nanoTime() - startTimeNs))
                );

                return new HTTPResponse(
                    responseStatusCode,
                    "application/json",
                    response.toPrettyString().getBytes()
                );
            } else {
                String cacheKey = path;
                CachedFile cachedFile = fileCache.get(cacheKey);

                var file = new File(frontendDir, path);
                if (!file.exists() || !file.isFile()) {
                    file = new File(frontendDir, "index.html");
                    cacheKey = "index.html";
                    cachedFile = fileCache.get(cacheKey);

                    if (!file.exists()) {
                        return new HTTPResponse(
                            404,
                            "text/plain",
                            ("Not Found: " +
                                path +
                                " and index.html missing").getBytes()
                        );
                    }
                }

                if (
                    cachedFile == null ||
                    file.lastModified() > cachedFile.lastModified
                ) {
                    try {
                        byte[] content = Files.readAllBytes(file.toPath());
                        String contentType = mimeType(file.getName());

                        addToCache(
                            cacheKey,
                            content,
                            contentType,
                            file.lastModified()
                        );

                        String rangeHeader = https
                            .getRequestHeaders()
                            .getFirst("Range");
                        if (
                            rangeHeader != null &&
                            rangeHeader.startsWith("bytes=")
                        ) {
                            String rangeValue = rangeHeader.substring(
                                "bytes=".length()
                            );
                            String[] ranges = rangeValue.split("-");

                            if (ranges.length == 2) {
                                try {
                                    long rangeStart = Long.parseLong(ranges[0]);
                                    long rangeEnd = ranges[1].isEmpty()
                                        ? content.length - 1
                                        : Long.parseLong(ranges[1]);

                                    if (
                                        rangeStart >= 0 &&
                                        rangeEnd < content.length &&
                                        rangeStart <= rangeEnd
                                    ) {
                                        return new HTTPResponse(
                                            206,
                                            contentType,
                                            content,
                                            rangeStart,
                                            rangeEnd
                                        );
                                    }
                                } catch (NumberFormatException e) {
                                    System.err.println(
                                        "Invalid range format: " + rangeHeader
                                    );
                                }
                            }
                        }

                        return new HTTPResponse(200, contentType, content);
                    } catch (IOException e) {
                        System.err.println(
                            "Error reading file: " +
                            file.getPath() +
                            " - " +
                            e.getMessage()
                        );
                        return new HTTPResponse(
                            500,
                            "text/plain",
                            ("Error reading file: " + e.getMessage()).getBytes()
                        );
                    }
                } else {
                    cachedFile.updateLastAccessed();

                    String ifNoneMatch = https
                        .getRequestHeaders()
                        .getFirst("If-None-Match");
                    if (
                        ifNoneMatch != null &&
                        ifNoneMatch.equals(cachedFile.eTag)
                    ) {
                        return new HTTPResponse(
                            304,
                            cachedFile.contentType,
                            new byte[0]
                        );
                    }

                    String rangeHeader = https
                        .getRequestHeaders()
                        .getFirst("Range");
                    if (
                        rangeHeader != null && rangeHeader.startsWith("bytes=")
                    ) {
                        String rangeValue = rangeHeader.substring(
                            "bytes=".length()
                        );
                        String[] ranges = rangeValue.split("-");

                        if (ranges.length == 2) {
                            try {
                                long rangeStart = Long.parseLong(ranges[0]);

                                long rangeEnd = ranges[1].isEmpty()
                                    ? cachedFile.content.length - 1
                                    : Long.parseLong(ranges[1]);

                                if (
                                    rangeStart >= 0 &&
                                    rangeEnd < cachedFile.content.length &&
                                    rangeStart <= rangeEnd
                                ) {
                                    return new HTTPResponse(
                                        206,
                                        cachedFile.contentType,
                                        cachedFile.content,
                                        rangeStart,
                                        rangeEnd
                                    );
                                }
                            } catch (NumberFormatException e) {
                                System.err.println(
                                    "Invalid range format: " + rangeHeader
                                );
                            }
                        }
                    }

                    return new HTTPResponse(
                        200,
                        cachedFile.contentType,
                        cachedFile.content
                    );
                }
            }
        } catch (IllegalArgumentException | SecurityException e) {
            int statusCode = (e instanceof SecurityException) ? 403 : 400;
            System.err.println(
                "Request Error (" + statusCode + "): " + e.getMessage()
            );
            var n = new ObjectNode(null);
            n.set("error class", new TextNode(e.getClass().getName()));
            n.set("message", new TextNode(e.getMessage()));
            return new HTTPResponse(
                statusCode,
                "application/json",
                n.toPrettyString().getBytes()
            );
        } catch (Throwable err) {
            err.printStackTrace();
            var n = new ObjectNode(null);
            n.set("error class", new TextNode(err.getClass().getName()));
            n.set("message", new TextNode(err.getMessage()));
            var a = new ArrayNode(null);
            for (var e : err.getStackTrace()) {
                var se = new ObjectNode(null);
                se.set("line number", new IntNode(e.getLineNumber()));
                se.set("class name", new TextNode(e.getClassName()));
                se.set("method name", new TextNode(e.getMethodName()));
                a.add(se);
            }
            n.set("stack trace", a);
            return new HTTPResponse(
                500,
                "application/json",
                n.toPrettyString().getBytes()
            );
        } finally {
            if (user != null) {
                nbRequestsInProgress.remove(user);
            }
        }
    }

    private List<NodeEndpoint> endpoints(
        String endpointName,
        BNode currentNode
    ) {
        if (endpointName.endsWith("/")) {
            endpointName = endpointName.substring(0, endpointName.length() - 1);
        }

        if (endpointName.isEmpty()) {
            if (currentNode == null) {
                System.err.println(
                    "Warning: No current node for user, returning endpoints for graph root."
                );
                currentNode = graph.root();
            }

            return graph
                .endpointsUsableFrom(currentNode)
                .stream()
                .filter(e -> e instanceof View)
                .toList();
        } else {
            var e = graph.findEndpoint(endpointName);

            if (e == null) {
                throw new IllegalArgumentException(
                    "no such endpoint: " + endpointName
                );
            }

            if (currentNode != null && !currentNode.matches(e)) {
                throw new IllegalArgumentException(
                    "Endpoint " +
                    endpointName +
                    " is not applicable to the current node: " +
                    currentNode
                );
            }

            return List.of(e);
        }
    }

    static String mimeType(String url) {
        if (url == null) return "application/octet-stream";

        String lowerUrl = url.toLowerCase();

        if (lowerUrl.endsWith(".html") || lowerUrl.endsWith(".htm")) {
            return "text/html; charset=utf-8";
        } else if (lowerUrl.endsWith(".js") || lowerUrl.endsWith(".jsx")) {
            return "text/javascript; charset=utf-8";
        } else if (lowerUrl.endsWith(".jpg") || lowerUrl.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (lowerUrl.endsWith(".png")) {
            return "image/png";
        } else if (lowerUrl.endsWith(".svg")) {
            return "image/svg+xml";
        } else if (lowerUrl.endsWith(".css")) {
            return "text/css; charset=utf-8";
        } else if (lowerUrl.endsWith(".json")) {
            return "application/json; charset=utf-8";
        } else if (lowerUrl.endsWith(".txt")) {
            return "text/plain; charset=utf-8";
        } else if (lowerUrl.endsWith(".ico")) {
            return "image/x-icon";
        } else if (
            lowerUrl.endsWith(".webmanifest") || lowerUrl.endsWith(".manifest")
        ) {
            return "application/manifest+json";
        } else if (lowerUrl.endsWith(".ttf")) {
            return "font/ttf";
        } else if (lowerUrl.endsWith(".woff")) {
            return "font/woff";
        } else if (lowerUrl.endsWith(".woff2")) {
            return "font/woff2";
        } else {
            System.err.println(
                "Warning: Unknown MIME type for file: " +
                url +
                ". Defaulting to application/octet-stream."
            );
            return "application/octet-stream";
        }
    }

    private static ObjectNode grabInputFromURLandPOST(HttpExchange http)
        throws IOException {
        var postData = http.getRequestBody().readAllBytes();
        ObjectNode inputJson = null;

        try {
            inputJson = postData.length > 0
                ? (ObjectNode) mapper.readTree(postData)
                : new ObjectNode(null);
        } catch (Exception e) {
            System.err.println(
                "Failed to parse POST body as JSON: " + new String(postData)
            );
            inputJson = new ObjectNode(null);
        }

        var query = query(http.getRequestURI().getQuery());
        ObjectNode finalInputJson = inputJson;
        query.forEach((key, value) -> {
            if (!finalInputJson.has(key)) {
                finalInputJson.set(key, new TextNode(value));
            } else {
                System.err.println(
                    "Warning: URL parameter '" +
                    key +
                    "' conflicts with POST data key. POST data takes precedence."
                );
            }
        });

        return finalInputJson;
    }

    private static Map<String, String> query(String s) {
        Map<String, String> query = new HashMap<>();

        if (s != null && !s.isEmpty()) {
            for (var e : TextUtilities.split(s, '&')) {
                if (e.isEmpty()) continue;
                var a = e.split("=", 2);
                if (a.length > 0 && !a[0].isEmpty()) {
                    try {
                        String key = java.net.URLDecoder.decode(
                            a[0],
                            java.nio.charset.StandardCharsets.UTF_8
                        );
                        String value = (a.length == 2)
                            ? java.net.URLDecoder.decode(
                                a[1],
                                java.nio.charset.StandardCharsets.UTF_8
                            )
                            : "";
                        query.put(key, value);
                    } catch (IllegalArgumentException decodeEx) {
                        System.err.println(
                            "Warning: Failed to decode URL parameter: " +
                            e +
                            " - " +
                            decodeEx.getMessage()
                        );
                    }
                }
            }
        }

        return query;
    }

    //	static String filename = "/Users/lhogie/a/job/i3s/tableau_de_bord/self-signed-certificate/keystore.jks";

    private SSLContext getSslContext() throws Exception {
        var keyStore = KeyStore.getInstance("JKS");
        //			InputStream fis = new FileInputStream(filename);
        var fis = WebServer.class.getResourceAsStream("/web/keystore.jks");
        var password = "password".toCharArray();
        try {
            keyStore.load(fis, password);
        } finally {
            if (fis != null) {
                fis.close();
            }
        }

        var keyManagerFactory = KeyManagerFactory.getInstance(
            KeyManagerFactory.getDefaultAlgorithm()
        );
        keyManagerFactory.init(keyStore, password);
        var sslContext = SSLContext.getInstance("TLS");
        sslContext.init(
            keyManagerFactory.getKeyManagers(),
            null,
            new SecureRandom()
        );
        return sslContext;
    }

    public void log(String msg) {
        logs.add(new Log(new Date(), msg));
    }

    public static class Info extends NodeEndpoint<WebServer> {

        @Override
        public String whatItDoes() {
            return "Provides information about the WebServer node.";
        }

        public Info(BBGraph db) {
            super(db);
        }

        public Info(BBGraph db, int id) {
            super(db, id);
        }

        @Override
        public EndpointResponse exec(
            ObjectNode in,
            User user,
            WebServer webServer,
            HttpsExchange exchange,
            WebServer n
        ) {
            var r = new ObjectNode(null);
            r.set(
                "#request NOW",
                new TextNode("" + n.nbRequestsInProgress.size())
            );
            r.set(
                "#requests",
                new TextNode(
                    "" +
                    n.nbRequestsInProgress
                        .stream()
                        .map(uu -> uu.name.get())
                        .toList()
                )
            );
            r.set(
                "active users",
                new TextNode(
                    "" +
                    n
                        .activeUsers()
                        .l.stream()
                        .map(uu -> uu.name.get())
                        .toList()
                )
            );
            r.set(
                "timeout",
                new TextNode(
                    "" +
                    n.httpsServer
                        .getHttpsConfigurator()
                        .getSSLContext()
                        .getClientSessionContext()
                        .getSessionTimeout()
                )
            );
            r.set(
                "cache size",
                new TextNode(
                    "" +
                    n.httpsServer
                        .getHttpsConfigurator()
                        .getSSLContext()
                        .getClientSessionContext()
                        .getSessionCacheSize()
                )
            );
            r.set(
                "SSL protocol",
                new TextNode(
                    n.httpsServer
                        .getHttpsConfigurator()
                        .getSSLContext()
                        .getProtocol()
                )
            );
            return new EndpointJsonResponse(r, "nodeinfo");
        }
    }

    public static class Logs extends NodeEndpoint<WebServer> {

        @Override
        public String whatItDoes() {
            return "providing a view of the logs for the WebServer node";
        }

        public Logs(BBGraph db) {
            super(db);
        }

        public Logs(BBGraph db, int id) {
            super(db, id);
        }

        @Override
        public EndpointResponse exec(
            ObjectNode in,
            User user,
            WebServer webServer,
            HttpsExchange exchange,
            WebServer n
        ) {
            var r = new ArrayNode(null);
            n.logs.forEach(l -> {
                var lr = new ObjectNode(null);
                lr.set("date", new TextNode(l.date.toString()));
                lr.set("message", new TextNode(l.msg));
                r.add(lr);
            });
            return new EndpointJsonResponse(r, "logs");
        }
    }

    public static class EndpointCallDistributionView
        extends NodeEndpoint<WebServer> {

        @Override
        public String whatItDoes() {
            return "Provides a distribution view of endpoint calls for the WebServer node.";
        }

        public EndpointCallDistributionView(BBGraph db) {
            super(db);
        }

        public EndpointCallDistributionView(BBGraph db, int id) {
            super(db, id);
        }

        @Override
        public EndpointResponse exec(
            ObjectNode in,
            User user,
            WebServer webServer,
            HttpsExchange exchange,
            WebServer ws
        ) {
            var d = new Byransha.Distribution();
            graph
                .findAll(NodeEndpoint.class, e -> true)
                .forEach(e -> d.addXY(e.name(), e.nbCalls));
            return new EndpointJsonResponse(d.toJson(), "logs");
        }
    }

    @Override
    public String prettyName() {
        return "HTTP server";
    }
}
