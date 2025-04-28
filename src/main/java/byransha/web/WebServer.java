package byransha.web;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;

import byransha.*;
import byransha.web.endpoint.*;
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

import byransha.BBGraph;
import byransha.BNode;
import byransha.Byransha;
import byransha.JVMNode;
import byransha.ListNode;
import byransha.Log;
import byransha.OSNode;
import byransha.StringNode;
import byransha.UI;
import byransha.User;
import byransha.User.History;
import byransha.graph.AnyGraph;
import byransha.labmodel.I3S;
import byransha.labmodel.model.v0.EtatCivil;
import byransha.labmodel.model.v0.Person;
import byransha.labmodel.model.v0.Picture;
import byransha.labmodel.model.v0.view.LabView;
import byransha.labmodel.model.v0.view.StructureView;
import byransha.web.endpoint.Authenticate;
import byransha.web.endpoint.Edit;
import byransha.web.endpoint.Endpoints;
import byransha.web.endpoint.IntrospectingEndpoint;
import byransha.web.endpoint.Jump;
import byransha.web.endpoint.NodeEndpoints;
import byransha.web.endpoint.NodeInfo;
import byransha.web.endpoint.Nodes;
import byransha.web.endpoint.SetValue;
import byransha.web.endpoint.Summarizer;
import byransha.web.view.CharExampleXY;
import byransha.web.view.CharacterDistribution;
import byransha.web.view.ModelDOTView;
import byransha.web.view.ModelGraphivzSVGView;
import byransha.web.view.SourceView;
import byransha.web.view.ToStringView;
import toools.reflect.ClassPath;
import toools.text.TextUtilities;

/**
 * https://syncagio.medium.com/how-to-setup-java-httpsserver-and-keystore-eb74a8bd89d
 */

public class WebServer extends BNode {
	public static File defaultDBDirectory = new File(
			System.getProperty("user.home") + "/." + BBGraph.class.getPackageName());

	public static void main(String[] args) throws Exception {
		var argList = List.of(args);
		var argMap = new HashMap<String, String>();
		// argMap.put("--createDB", "true");
		argList.stream().map(a -> a.split("=")).forEach(a -> argMap.put(a[0], a[1]));
		BBGraph g = instantiateGraph(argMap);
		int port = Integer.valueOf(argMap.getOrDefault("-port", "8080"));
		new WebServer(g, port);
	}

	public static BBGraph instantiateGraph(Map<String, String> argMap)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, ClassNotFoundException, IOException {

		if (defaultDBDirectory.exists()) {
			var g = (BBGraph) Class.forName(Files.readString(new File(defaultDBDirectory, "dbClass.txt").toPath()))
					.getConstructor(File.class).newInstance(defaultDBDirectory);
			System.out.println("loading DB from " + defaultDBDirectory);
//			var g = new BBGraph(defaultDBDirectory);

			g.loadFromDisk(n -> System.out.println("loading node " + n),
					(n, s) -> System.out.println("loading arc " + n + ", " + s));
			return g;
		} else if (argMap.containsKey("--createDB")) {
			return new BBGraph(defaultDBDirectory);
		} else {
			var g = new BBGraph(null);
			var p = new Person(g);
			p.etatCivil = new EtatCivil(g);
			p.etatCivil.name = new StringNode(g, "Caro");
			p.etatCivil.firstName = new StringNode(g, "George");
			p.etatCivil.nationality = new StringNode(g, "FR");
			var lab = new I3S(g);
			lab.members.add(p);
			lab.director = p;
			return g;
		}
	}

	static ObjectMapper mapper = new ObjectMapper();

	final JVMNode jvm;
	final Byransha byransha;
	final OSNode operatingSystem;

	List<User> nbRequestsInProgress = Collections.synchronizedList(new ArrayList<>());

	private HttpsServer httpsServer;
	public final List<Log> logs = new ArrayList<>();

	public WebServer(BBGraph g, int port) throws Exception {

		super(g);
		jvm = g.find(JVMNode.class, e -> true) == null ? new JVMNode(g) : g.find(JVMNode.class, e -> true);
		byransha = g.find(Byransha.class, e -> true) == null ? new Byransha(g) : g.find(Byransha.class, e -> true);
		operatingSystem = g.find(OSNode.class, e -> true) == null ? new OSNode(g) : g.find(OSNode.class, e -> true);
		new NodeInfo(g);
		new Views(g);
		new Jump(g);
		new Endpoints(g);
		new JVMNode.Kill(g);
		new Authenticate(g);
		new Nodes(g);
		new EndpointCallDistributionView(g);
		new Info(g);
		new Logs(g);
		new BasicView(g);
		new CharacterDistribution(g);
		new CharExampleXY(g);
		new User.UserView(g);
		new BBGraph.GraphNivoView(g);
		new OSNode.View(g);
		new JVMNode.View(g);
		new BNode.InOutsNivoView(g);
		new ModelGraphivzSVGView(g);
		new Navigator(g);
		new OutNodeDistribution(g);
		new Picture.V(g);
		new LabView(g);
		new ModelDOTView(g);
		new SourceView(g);
		new ToStringView(g);
		new StructureView(g);
		new NodeEndpoints(g);
		new SetValue(g);
		new AnyGraph.Classes(g);
		new Edit(g);
		new IntrospectingEndpoint(g);
		new History(g);
		new UI(g);
		new UI.getProperties(g);
		new Summarizer(g);
		new LoadImage(g);

		try {
			Path classPathFile = new File(Byransha.class.getPackageName() + "-classpath.lst").toPath();
			System.out.println("writing " + classPathFile);
			Files.write(classPathFile, ClassPath.retrieveSystemClassPath().toString().getBytes());
		} catch (IOException err) {
			err.printStackTrace();
		}

		System.out.println("starting HTTP server on port " + port);
		httpsServer = HttpsServer.create(new InetSocketAddress(port), 0);
		httpsServer.setHttpsConfigurator(new HttpsConfigurator(getSslContext()) {
			@Override
			public void configure(HttpsParameters params) {
				try {
					SSLContext context = getSSLContext();
					SSLEngine engine = context.createSSLEngine();
					params.setNeedClientAuth(false);
					params.setCipherSuites(engine.getEnabledCipherSuites());
					params.setProtocols(engine.getEnabledProtocols());
					SSLParameters sslParameters = context.getSupportedSSLParameters();
					params.setSSLParameters(sslParameters);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});

		httpsServer.createContext("/", http -> processRequest((HttpsExchange) http).send(http));
		httpsServer.setExecutor(Executors.newCachedThreadPool());
		httpsServer.start();
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


	private HTTPResponse processRequest(HttpsExchange https) {
		User user = null;
		boolean needsTokenCookie = false;

		try {
			long startTimeNs = System.nanoTime();
			ObjectNode inputJson = grabInputFromURLandPOST(https);
			final var inputJson2sendBack = inputJson.deepCopy();

			String userToken = null;
			String cookieHeader = https.getRequestHeaders().getFirst("Cookie");

			if (cookieHeader != null) {
				for (String cookie : cookieHeader.split(";")) {
					cookie = cookie.trim();
					if (cookie.startsWith("user_token=")) {
						userToken = cookie.substring("user_token=".length());
						break;
					}
				}
			}

			if (userToken != null) {
				user = graph.findUserByToken(userToken);
			}

			if (user == null) {
				user = Authenticate.setDefaultUser(graph, https);
			} else {
//				System.out.println("found user from token : " + user);
			}

			var path = https.getRequestURI().getPath();

			if (!path.endsWith("/")) {
				path += "/";
			}

			if (path.startsWith("/api/")) {
				nbRequestsInProgress.add(user);

				var response = new ObjectNode(null);
				response.set("backend version", new TextNode(Byransha.VERSION));
				long uptimeMs = ManagementFactory.getRuntimeMXBean().getUptime();
				response.set("uptimeMs", new TextNode(Duration.ofMillis(uptimeMs).toString()));

				// response.set("compliant_endpoints",
				// graph.findEndpoint(Endpoints.class).exec(new ObjectNode(null), user, this,
				// https).toJson());

				if (!inputJson2sendBack.isEmpty())
					response.set("request", inputJson2sendBack);

				if (user != null) {
					response.set("username", new TextNode(user.name.get()));
					response.set("user_id", new IntNode(user.id()));
					response.set("user_token", new TextNode(user.token));
				}

				var endpoints = endpoints(path.substring(5), user.currentNode());
//				System.err.println(endpoints);

				if (inputJson.remove("raw") != null) {
					if (endpoints.size() != 1)
						throw new IllegalArgumentException("only 1 endpoint allowed");

					if (inputJson.size() > 0)
						throw new IllegalArgumentException("parms unused: " + inputJson.toPrettyString());

					var endpoint = endpoints.get(0);
					var result = endpoint.exec(inputJson, user, this, https);
					return new HTTPResponse(200, result.contentType, result.toRawText().getBytes());
				} else {
					var resultsNode = new ArrayNode(null);
					response.set("results", resultsNode);

					for (var endpoint : endpoints) {
						ObjectNode er = new ObjectNode(null);
						er.set("endpoint", new TextNode(endpoint.name()));
						er.set("endpoint_class", new TextNode(endpoint.getClass().getName()));
						er.set("response_type", new TextNode(endpoint.type().name()));
						er.set("pretty_name", new TextNode(endpoint.prettyName()));
						er.set("what_is_this", new TextNode(endpoint.whatIsThis()));
						long startTimeNs2 = System.nanoTime();

						try {
							EndpointResponse<?> result = endpoint.exec(inputJson, user, this, https);
							er.set("result", result.toJson());
						} catch (Throwable err) {
							err.printStackTrace();
							var sw = new StringWriter();
							err.printStackTrace(new PrintWriter(sw));
							er.set("error", new TextNode(sw.toString()));
						}

						double duration = System.nanoTime() - startTimeNs2;
						synchronized (endpoint) {
							endpoint.nbCalls++;
							endpoint.timeSpentNs += duration;
						}
						er.set("durationNs", new DoubleNode(duration));
						resultsNode.add(er);
					}
				}

				response.set("durationNs", new TextNode("" + (System.nanoTime() - startTimeNs)));

				if (!inputJson.isEmpty())
					throw new IllegalArgumentException("parms unused: " + inputJson.toPrettyString());

				return new HTTPResponse(200, "text/json", response.toPrettyString().getBytes());
			} else {
				var file = new File(frontendDir, path);

				if (!file.exists() || !file.isFile()) {
					file = new File(frontendDir, "index.html");
				}

//				System.out.println("serving " + file);
				return new HTTPResponse(200, mimeType(file.getName()), Files.readAllBytes(file.toPath()));
			}
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
			return new HTTPResponse(500, "text/plain", n.toPrettyString().getBytes());
		} finally {
			// Remove the user from nbRequestsInProgress if it was added
			if (user != null) {
				nbRequestsInProgress.remove(user);
			}
		}
	}

	private List<NodeEndpoint> endpoints(String endpointName, BNode currentNode) {
		if (endpointName.endsWith("/")) {
			endpointName = endpointName.substring(0, endpointName.length() - 1);
		}

		if (endpointName == null || endpointName.isEmpty()) {
			return graph.endpointsUsableFrom(currentNode).stream().filter(e -> e instanceof View).toList();
		} else {
			var e = graph.findEndpoint(endpointName);

			if (e == null) {
				throw new IllegalArgumentException("no such endpoint: " + endpointName);
			}

			return List.of(e);
		}
	}

	static String mimeType(String url) {
		if (url.endsWith(".html") || url.endsWith(".htm")) {
			return "text/html";
		} else if (url.endsWith(".js") || url.endsWith(".jsx")) {
			return "text/javascript";
		} else if (url.endsWith(".jpg") || url.endsWith(".jpeg")) {
			return "image/jpeg";
		} else if (url.endsWith(".png")) {
			return "image/png";
		} else if (url.endsWith(".svg")) {
			return "image/svg+xml";
		} else if (url.endsWith(".css")) {
			return "text/css";
		} else {
			return null;
		}
	}

	private static ObjectNode grabInputFromURLandPOST(HttpExchange http) throws IOException {
		// gets the date from POST
		var postData = http.getRequestBody().readAllBytes();
		ObjectNode inputJson = postData.length > 0 ? (ObjectNode) mapper.readTree(postData) : new ObjectNode(null);

		// adds the URL parameters from the query string to the JSON
		var query = query(http.getRequestURI().getQuery());
		query.entrySet().forEach(e -> inputJson.set(e.getKey(), new TextNode(e.getValue())));

		return inputJson;
	}

	private static Map<String, String> query(String s) {
		Map<String, String> query = new HashMap<>();

		if (s != null && !s.isEmpty()) {
			for (var e : TextUtilities.split(s, '&')) {
				var a = e.split("=");
				query.put(a[0], a.length == 2 ? a[1] : null);
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

		var keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		keyManagerFactory.init(keyStore, password);
		var sslContext = SSLContext.getInstance("TLS");
		sslContext.init(keyManagerFactory.getKeyManagers(), null, new SecureRandom());
		return sslContext;
	}

	public void log(String msg) {
		logs.add(new Log(new Date(), msg));
	}

	public static class Info extends NodeEndpoint<WebServer> {

		@Override
		public String whatIsThis() {
			return "Provides information about the WebServer node.";
		}

		public Info(BBGraph db) {
			super(db);
		}

		public Info(BBGraph db, int id) {
			super(db, id);
		}

		@Override
		public EndpointResponse exec(ObjectNode in, User user, WebServer webServer, HttpsExchange exchange,
				WebServer n) {
			var r = new ObjectNode(null);
			r.set("#request NOW", new TextNode("" + n.nbRequestsInProgress.size()));
			r.set("#requests", new TextNode("" + n.nbRequestsInProgress.stream().map(uu -> uu.name.get()).toList()));
			r.set("active users", new TextNode("" + n.activeUsers().l.stream().map(uu -> uu.name.get()).toList()));
			r.set("timeout", new TextNode("" + n.httpsServer.getHttpsConfigurator().getSSLContext()
					.getClientSessionContext().getSessionTimeout()));
			r.set("cache size", new TextNode("" + n.httpsServer.getHttpsConfigurator().getSSLContext()
					.getClientSessionContext().getSessionCacheSize()));
			r.set("SSL protocol",
					new TextNode("" + n.httpsServer.getHttpsConfigurator().getSSLContext().getProtocol()));
			return new EndpointJsonResponse(r, "nodeinfo");
		}
	}

	public static class Logs extends NodeEndpoint<WebServer> {
		@Override
		public String whatIsThis() {
			return "Provides a view of the logs for the WebServer node.";
		}

		public Logs(BBGraph db) {
			super(db);
		}

		public Logs(BBGraph db, int id) {
			super(db, id);
		}

		@Override
		public EndpointResponse exec(ObjectNode in, User user, WebServer webServer, HttpsExchange exchange,
				WebServer n) {
			var r = new ArrayNode(null);
			n.logs.forEach(l -> {
				var lr = new ObjectNode(null);
				lr.set("date", new TextNode(l.date.toLocaleString()));
				lr.set("message", new TextNode(l.msg));
				r.add(lr);
			});
			return new EndpointJsonResponse(r, "logs");
		}
	}

	public static class EndpointCallDistributionView extends NodeEndpoint<WebServer> {
		@Override
		public String whatIsThis() {
			return "Provides a distribution view of endpoint calls for the WebServer node.";
		}

		public EndpointCallDistributionView(BBGraph db) {
			super(db);
		}

		public EndpointCallDistributionView(BBGraph db, int id) {
			super(db, id);
		}

		@Override
		public EndpointResponse exec(ObjectNode in, User user, WebServer webServer, HttpsExchange exchange,
				WebServer ws) {
			var d = new Byransha.Distribution();
			graph.findAll(NodeEndpoint.class, e -> true).forEach(e -> d.addXY(e.name(), e.nbCalls));
			return new EndpointJsonResponse(d.toJson(), "logs");
		}
	}

	@Override
	public String prettyName() {
		return "HTTP server";
	}

}
