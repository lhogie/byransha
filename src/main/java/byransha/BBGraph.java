package byransha;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.graph.AnyGraph;
import byransha.web.EndpointJsonResponse;
import byransha.web.EndpointJsonResponse.dialects;
import byransha.web.EndpointResponse;
import byransha.web.EndpointTextResponse;
import byransha.web.NodeEndpoint;
import byransha.web.TechnicalView;
import byransha.web.WebServer;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import toools.reflect.Clazz;

public class BBGraph extends BNode {
	public static Consumer<File> sysoutPrinter = f -> System.out.println("writing " + f.getAbsolutePath());
	public final File directory;
	public final List<BNode> nodes;
	private Map<Class<? extends BNode>, List<BNode>> byClass;
	private Int2ObjectMap<BNode> byID;// = new Int2ObjectOpenHashMap<>();
	int idCount = 1;

	StringNode testString;
	BooleanNode testBoolean;

	@Override
	public String whatIsThis() {
		return "BBGraph: A graph representation for BNodes.";
	}

	public BBGraph(File directory) {
		super(null, 0); // The graph has automatically ID 0
		this.directory = directory;
		nodes = new ArrayList<BNode>();
		accept(this); // self accept

	}

	public List<NodeEndpoint> endpointsUsableFrom(BNode n) {
		List<NodeEndpoint> r = new ArrayList<>();

		for (var v : findAll(NodeEndpoint.class, e -> true)) {
			if (v.getTargetNodeType().isAssignableFrom(n.getClass())) {
				r.add(v);
			}
		}

		Collections.sort(r, (a, b) -> a.getTargetNodeType().isAssignableFrom(b.getTargetNodeType()) ? 1 : -1);
		return r;
	}

	public synchronized int nextID() {
		for (int i = idCount;; ++i) {
			if (findByID(i) == null) {
				idCount = i + 1;
				return i;
			}
		}
	}

	public List<InLink> findRefsTO(BNode searchedNode) {
		var r = new ArrayList<InLink>();

		forEachNode(n -> {
			n.forEachOut((role, outNode) -> {
				if (outNode == searchedNode) {
					r.add(new InLink(role, n));
				}
			});
		});

		return r;
	}

	public void loadFromDisk(Consumer<BNode> newNodeInstantiated, BiConsumer<BNode, String> setRelation) {
		instantiateNodes(newNodeInstantiated);
		forEachNode(n -> {
			if (n instanceof PersistingNode pn)
				loadOuts(pn, setRelation);
		});
	}

	/*
	 * Loads all nodes from all class directories from the disk
	 */
	private void instantiateNodes(Consumer<BNode> newNodeInstantiated) {
		File[] files = directory.listFiles();
		if (files == null)
			return;
		else {
			for (File classDir : files) {
				String className = classDir.getName();
				var nodeClass = (Class<? extends BNode>) Clazz.findClassOrFail(className);
				if (nodeClass.equals(WebServer.class)) {
					System.err.println("Skipping WebServer class " + nodeClass.getName());
					continue;
				}

				for (File nodeDir : classDir.listFiles()) {
					int id = Integer.valueOf(nodeDir.getName().substring(1));

					// don't create the graph node twice!
					if (id != 0) {
						try {
							var constructor = nodeClass.getConstructor(BBGraph.class, int.class);
							BNode node = constructor.newInstance(graph, id);
							newNodeInstantiated.accept(node);
						} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
								| InvocationTargetException | SecurityException err) {
							throw new RuntimeException(err);
						} catch (NoSuchMethodException e) {
							System.out.println("Warning: No constructor found for class " + nodeClass.getName() + ": "
									+ e.getMessage());
						}
					}
				}
			}
		}
	}

	private void loadOuts(PersistingNode node, BiConsumer<BNode, String> setRelation) {
		var d = node.outsDirectory();

		if (!d.exists())
			return;

		File[] files = d.listFiles();
		if (files == null) {
			System.err.println("Warning: Could not list files in directory: " + d.getAbsolutePath());
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
					int id = Integer.valueOf(fn.substring(1));// atIndex + 1
					BNode targetNode = findByID(id);

					if (targetNode == null) {
						System.err.println("Warning: Could not find node with ID: " + id);
						continue;
					}

					try {
						if (node instanceof ListNode<?>) {
							((ListNode<BNode>) node).add(targetNode);
						} else {
							try {
								if (node.hasField(symlink.getName())) {
									node.setField(symlink.getName(), targetNode);
								}
							} catch (Exception e) {
								System.err.println("Error setting field " + symlink.getName() + " for node " + node
										+ ": " + e.getMessage());
							}
						}
						setRelation.accept(node, relationName);
					} catch (Exception e) {
						System.err.println(
								"Error setting relation " + relationName + " for node " + node + ": " + e.getMessage());
					}
				} catch (NumberFormatException e) {
					System.err.println("Error: Invalid node ID in filename: " + fn + ": " + e.getMessage());
				}
			} catch (IOException e) {
				System.err.println("Error reading symbolic link: " + symlink.getPath() + ": " + e.getMessage());
			} catch (Exception e) {
				System.err.println("Unexpected error processing symlink " + symlink.getPath() + ": " + e.getMessage());
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
		synchronized (nodes) {
			for (var n : nodes) {
//			System.err.println(n.getClass());
//			System.err.println(nodes.size());
//			System.err.println(nodes.size());

				// Skip WebServer nodes to prevent circular references
				if (!(n instanceof WebServer)) {
					h.accept(n);
				}
			}
		}
	}

	public void saveAll(Consumer<File> writingFiles) throws IOException {
		forEachNode(n -> {
			if (n instanceof ValuedNode<?> vn) {
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
		var r = new AtomicLong();

		forEachNode(n -> {
			r.incrementAndGet();
		});

		return r.get();
	}

	synchronized void accept(BNode n) {
		if (n instanceof NodeEndpoint ne) {
			var alreadyIn = findEndpoint(ne.getClass());

			if (alreadyIn != null)
				throw new IllegalArgumentException("adding " + ne + ", " + "endpoint with same class '"
						+ ne.getClass().getName() + "' already there: " + alreadyIn.getClass().getName());

			alreadyIn = findEndpoint(ne.name());

			if (alreadyIn != null)
				throw new IllegalArgumentException("adding " + ne + ", " + "endpoint with same name '"
						+ ne.getClass().getName() + "' already there: " + alreadyIn.getClass().getName());

		}

		var already = findByID(n.id());

		if (already != null)
			throw new IllegalStateException("can't add node " + n + " because its ID is already taken by: " + already);

		synchronized (nodes) {
			nodes.add(n);

			if (n instanceof PersistingNode pn && pn.directory() != null) {
				pn.createOutSymLinks(BBGraph.sysoutPrinter);
			}
		}

		if (byClass != null) {
			synchronized (byClass) {
				var s = byClass.get(n.getClass());

				if (s == null) {
					s = byClass.put(n.getClass(), new ArrayList<>());
				}

				synchronized (s) {
					s.add(n);
				}
			}
		}

		if (byID != null) {
			synchronized (byID) {
				byID.put(n.id(), n);
			}
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
				System.err.println("Warning: Could not list files in directory: " + d.getAbsolutePath());
			}
		}

		System.out.println("Deleting " + d);
		boolean success = d.delete();
		if (!success) {
			System.err.println("Warning: Failed to delete " + d.getAbsolutePath());
			// Try to determine why deletion failed
			if (!d.exists()) {
				System.err.println("  File does not exist");
			} else if (!d.canWrite()) {
				System.err.println("  File is not writable");
			} else if (d.isDirectory() && d.list() != null && d.list().length > 0) {
				System.err.println("  Directory is not empty");
			}
		}
	}

//	public void delete(int id){
//		var targetNode = findByID(id);
//		if(targetNode != null){
//			System.out.println("Deleting node with ID: " + id + " (" + targetNode + ")");
//		}
//		else{
//			System.err.println("Node with ID: " + id + " not found.");
//		}
//	}

	public BNode findByID(int id) {
		if (byID != null) {
			return byID.get(id);
		} else {
			synchronized (nodes) {
				for (var n : nodes) {
					if (n.id() == id) {
						return n;
					}
				}
			}
		}

		return null;
	}

	public void saveRecursive(BNode node){
		ArrayDeque<BNode> queue = new ArrayDeque<>();
		Set<BNode> visited = new HashSet<>();

		queue.add(node);
		visited.add(node);

		while(!queue.isEmpty()){
			BNode currentNode = queue.poll();
			if(currentNode instanceof PersistingNode){
				((PersistingNode) currentNode).save(f -> {});
				currentNode.outs().forEach((s, outNode) -> {
					if(!visited.contains(outNode)){
						queue.add(outNode);
						visited.add(outNode);
					}
				});
			}
			else{
				System.out.println("Node is not an instance of PersistingNode: " + currentNode);
			}

			System.out.println("Outs of the current node " + currentNode + ": " + currentNode.outs());
		}
	}

	public synchronized <C extends BNode> C find(Class<C> nodeClass, Predicate<C> p) {
		var l = findAll(nodeClass, p);
		return l.isEmpty() ? null : l.getFirst();
	}

	public synchronized <C extends BNode> List<C> findAll(Class<C> nodeClass, Predicate<C> p) {
		var r = new ArrayList<C>();

		if (byClass != null) {
			for (var s : byClass.entrySet()) {
				if (nodeClass.isAssignableFrom(s.getKey())) {
					synchronized (s.getValue()) {
						for (var node : s.getValue()) {
							// ensure the node is of the correct type before casting
							if (nodeClass.isInstance(node)) {
								C nn = nodeClass.cast(node);

								if (p.test(nn)) {
									r.add(nn);
								}
							}
						}
					}
				}
			}
		} else {
			synchronized (nodes) {
				for (var node : nodes) {
					// Use isInstance to check if the node is of the correct type
					if (nodeClass.isInstance(node)) {
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
		synchronized (nodes) {
			return (List<User>) (List) nodes.stream().filter(n -> n instanceof User).toList();
		}
	}


	public User findUserByToken(String token) {
		return find(User.class, u -> u.token != null && u.token.equals(token));
	}

	public <N extends BNode, NE extends NodeEndpoint<N>> NE findEndpoint(Class<NE> c) {
		return find(c, e -> true);
	}

	public NodeEndpoint<?> findEndpoint(String name) {
		return (NodeEndpoint<?>) find(NodeEndpoint.class, e -> e.name().equalsIgnoreCase(name));
	}

	public static class DBView extends NodeEndpoint<BBGraph> implements TechnicalView {

		@Override
		public String whatIsThis() {
			return "DBView: A technical view for BBGraph.";
		}

		public DBView(BBGraph g) {
			super(g);
		}

		@Override
		public EndpointResponse exec(ObjectNode input, User user, WebServer webServer, HttpsExchange exchange,
				BBGraph node) throws Throwable {
			return new EndpointTextResponse("text/html", pw -> {
				pw.println("<ul>");
				pw.println("<li>" + graph.countNodes() + " nodes");
				synchronized (graph.nodes) {
					pw.println(
							"<li>Node classes: <ul>" + graph.nodes.stream().map(n -> "<li>" + n.getClass()).toList());
				}
				pw.println("</ul>");
				var users = graph.users();
				pw.println("<li>" + users.size() + " users: " + users.stream().map(u -> u.name.get()).toList());
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
		public String whatIsThis() {
			return "gives a NIVO text representing the graph";
		}

		public GraphNivoView(BBGraph db) {
			super(db);
		}

		public GraphNivoView(BBGraph db, int id) {
			super(db, id);
		}

		@Override
		public EndpointResponse exec(ObjectNode in, User user, WebServer webServer, HttpsExchange exchange,
				BBGraph db) {
			var g = new AnyGraph();

			db.forEachNode(v -> {
				g.addVertex(v.toVertex());
				v.forEachOut((s, o) -> {
					var a = g.newArc(g.ensureHasVertex(v), g.ensureHasVertex(o));
					a.label = s;
				});
			});

			return new EndpointJsonResponse(g.toNivoJSON(), dialects.nivoNetwork);
		}
	}

	@Override
	public String prettyName() {
		return "graph";
	}

}
