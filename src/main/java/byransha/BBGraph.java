package byransha;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.net.ssl.SSLSession;

import byransha.web.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpsExchange;

import byransha.graph.BGraph;
import byransha.web.EndpointJsonResponse.dialects;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import toools.reflect.Clazz;

public class BBGraph extends BNode {
	public static Consumer<File> sysoutPrinter = f -> System.out.println("writing " + f.getAbsolutePath());
	public final File directory;
	public final List<BNode> nodes;
	private Map<Class<? extends BNode>, List<BNode>> byClass;
	private Int2ObjectMap<BNode> byID;// = new Int2ObjectOpenHashMap<>();
	int idCount = 1;

	public BBGraph(File directory) {
		super(null);
		nodes = new ArrayList<BNode>();
		accept(this);
		new User(this, "user", "test");
		new User(this, "admin", "test");
		this.directory = directory;
	}

	public static class Ref {
		final String role;
		final BNode source;

		public Ref(String role, BNode c) {
			this.role = role;
			this.source = c;
		}

		@Override
		public String toString() {
			return source + "." + role;
		}
	}

	public List<Ref> findRefsTO(BNode searchedNode) {
		var r = new ArrayList<Ref>();

		forEachNode(n -> {
			n.forEachOut((role, outNode) -> {
				if (outNode == searchedNode) {
					r.add(new Ref(role, n));
				}
			});
		});

		return r;
	}

	public void load(Consumer<BNode> newNodeInstantiated, BiConsumer<BNode, String> setRelation) {
		instantiateNodes(newNodeInstantiated);
		forEachNode(n -> loadOuts(n, setRelation));
	}

	private void instantiateNodes(Consumer<BNode> newNodeInstantiated) {
		File[] files = directory.listFiles();
		if (files == null) return;
		else {
			for (File classDir : directory.listFiles()) {
				String className = classDir.getName();
				var nodeClass = (Class<? extends BNode>) Clazz.findClassOrFail(className);

				for (File nodeDir : classDir.listFiles()) {
					try {
						Constructor<? extends BNode> constructor = nodeClass.getConstructor(BBGraph.class);
						BNode node = constructor.newInstance(graph);
						node.setID(Integer.valueOf(nodeDir.getName().split("\\.")[1]));
						newNodeInstantiated.accept(node);
					} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
							 | InvocationTargetException | NoSuchMethodException | SecurityException err) {
						throw new RuntimeException(err);
					}
				}
			}
		}
	}

	private void loadOuts(BNode node, BiConsumer<BNode, String> setRelation) {
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
				int atIndex = fn.indexOf("@");
				if (atIndex == -1) {
					System.err.println("Warning: Invalid filename format for symlink: " + fn);
					continue;
				}

				try {
					int id = Integer.valueOf(fn.substring(atIndex + 1));
					BNode targetNode = findByID(id);

					if (targetNode == null) {
						System.err.println("Warning: Could not find node with ID: " + id);
						continue;
					}

					try {
						node.getClass().getField(relationName).set(node, targetNode);
						setRelation.accept(node, relationName);
					} catch (NoSuchFieldException e) {
						System.err.println("Error: Field '" + relationName + "' not found in class " + 
							node.getClass().getName() + ": " + e.getMessage());
					} catch (IllegalAccessException e) {
						System.err.println("Error: Cannot access field '" + relationName + "' in class " + 
							node.getClass().getName() + ": " + e.getMessage());
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
	 * Executes the given consumer for each node in the graph, except WebServer nodes.
	 * WebServer nodes are excluded to prevent circular references during serialization.
	 * Uses synchronization to prevent ConcurrentModificationException.
	 * 
	 * @param h The consumer to execute for each node
	 */
	public void forEachNode(Consumer<BNode> h) {
		synchronized (nodes) {
			for (var n : nodes) {
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

		forEachNode(n -> n.saveOuts(writingFiles));
		forEachNode(n -> n.saveIns(writingFiles));
	}

	public long countNodes() {
		var r = new AtomicLong();
		forEachNode(n -> {
			r.incrementAndGet();
		});
		return r.get();
	}

	synchronized void accept(BNode n) {
		var already = findByID(n.id());

		if (already != null)
			throw new IllegalStateException("can't add node " + n + " with id " + n.id() + " because of : " + already);

		synchronized (nodes) {
			nodes.add(n);
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

	public synchronized <C extends BNode> C find(Class<C> nodeClass, Predicate<C> p) {
		if (byClass != null) {
			for (var s : byClass.entrySet()) {
				if (nodeClass.isAssignableFrom(s.getKey())) {
					synchronized (s.getValue()) {
						for (var node : s.getValue()) {
							// Ensure the node is of the correct type before casting
							if (nodeClass.isInstance(node)) {
								C nn = nodeClass.cast(node);
								if (p.test(nn)) {
									return nn;
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
							return nn;
						}
					}
				}
			}
		}

		return null;
	}

	public List<User> users() {
		synchronized (nodes) {
			return (List<User>) (List) nodes.stream().filter(n -> n instanceof User).toList();
		}
	}

	public User findUser(SSLSession s) {
		return find(User.class, u -> u.session != null && Arrays.equals(u.session.getId(), s.getId()));
	}

	public static class DBView extends NodeEndpoint<BBGraph> implements TechnicalView {

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
					pw.println("<li>Node classes: <ul>" + graph.nodes.stream().map(n -> "<li>" + n.getClass()).toList());
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

	public static class GraphView extends NodeEndpoint<BBGraph> {

		public GraphView(BBGraph db) {
			super(db);
		}

		@Override
		public EndpointResponse exec(ObjectNode in, User user, WebServer webServer, HttpsExchange exchange,
				BBGraph db) {
			var g = new BGraph();

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

	public void incrementIDCount(){
		idCount++;
	}


}
