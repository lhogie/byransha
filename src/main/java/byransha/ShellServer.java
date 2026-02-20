package byransha;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;

import byransha.graph.BBGraph;
import byransha.graph.BNode;
import byransha.graph.NodeAction;
import byransha.graph.view.NodeView;

public class ShellServer {
	@FunctionalInterface
	interface CommandAction {
		void exec(PrintWriter out) throws Throwable;
	}

	record Command(String description, CommandAction action) {
	}

	private final Map<String, Command> commands = new HashMap<>();
	private final BBGraph graph;

	public ShellServer(BBGraph graph, int port) throws Throwable {
		this.graph = graph;
		initializeCommands(graph);

		new Thread(() -> {
			try {
				startServer(port);
			} catch (Throwable err) {
				throw err instanceof RuntimeException re ? re : new RuntimeException(err);
			}
		}).start();
	}

	private void initializeCommands(BBGraph graph) {
		commands.put("help", new Command("list available commands",
				out -> commands.forEach((name, cmd) -> out.println(name + " - " + cmd.description))));

		commands.put("whoami",
				new Command("print current user", out -> out.println(graph.systemNode.getCurrentUser())));

		commands.put("pwd", new Command("print current node", out -> out.println(currentNode())));

		commands.put("kill", new Command("kill the server's JVM", out -> System.exit(0)));
		commands.put("actions", new Command("list actions available on this node",
				out -> graph.currentUser().currentNode().actions().forEach(a -> out.println(a.commandName()))));
		commands.put("views", new Command("list views available on this node",
				out -> graph.currentUser().currentNode().views().forEach(a -> out.println(a.name()))));

		commands.put("ls", new Command("list outs",
				out -> currentNode().forEachOut((name, node) -> out.println(name + ": " + node))));

		commands.put("lf",
				new Command("list fields", out -> currentNode().forEachOutField(f -> out.println(f.getName()))));

		commands.put("id", new Command("print current node ID", out -> out.println(currentNode().id())));

		commands.put("name", new Command("print current node name", out -> out.println(currentNode())));
	}

	private void startServer(int port) throws Throwable {
		try (var serverSocket = new ServerSocket(port)) {
			System.out.println("Telnet server listening on port " + port);
			while (true) {
				// Using try-with-resources to automatically close client socket
				try (var clientSocket = serverSocket.accept();
						var in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
						var out = new PrintWriter(clientSocket.getOutputStream(), true)) {

					out.println("Welcome to Byransha!");
					
					while (true) {
						String line = in.readLine();

						if (line == null)
							break;

						line = line.trim();

						if (line.startsWith(".")) { // run action
							var cmdName = line.substring(1);
							var a = findAction(currentNode(), cmdName);

							if (a == null) {
								out.println(
										"no such action " + cmdName + " on node " + graph.currentUser().currentNode()
												+ " of " + graph.currentUser().currentNode().getClass());
							} else {
								var r = a.exec(graph.currentUser().currentNode());

								for (var v : r.result.views()) {
									out.println(v.prettyName() + ":");
									out.println(v.toJSON(graph.currentUser().currentNode()).toPrettyString());
								}

								out.println("*" + a.prettyName() + " completed in " + r.durationMs() + "ms:");
							}

						}
						if (line.startsWith("/")) { // show view
							var viewName = line.substring(1);
							var view = findView(currentNode(), viewName);

							if (view == null) {
								out.println("no such view " + viewName + " on node " + graph.currentUser().currentNode()
										+ " of " + graph.currentUser().currentNode().getClass());
							} else {
								var r = view.toJSON(currentNode());
								out.println(r.toPrettyString());
							}
						} else {
							Command cmd = commands.get(line);
							if (cmd != null) {
								try {
									cmd.action.exec(out);
								} catch (Throwable e) {
									e.printStackTrace();
								}
							} else {

								out.println("Unknown command: " + line);
							}

						}

						out.println();
					}
				} catch (IOException e) {
					System.err.println("Client error: " + e.getMessage());
				}
			}

		}
	}

	public static NodeAction<BNode, BNode> findAction(BNode n, String name) {
		for (var a : n.actions()) {
			if (a.commandName().equals(name)) {
				return a;
			}
		}

		return null;
	}

	public static NodeView<BNode> findView(BNode n, String name) {
		for (var v : n.views()) {
			if (v.name().equals(name)) {
				return v;
			}
		}

		return null;
	}

	private BNode currentNode() {
		return graph.systemNode.getCurrentUser().currentNode();
	}
}