package byransha;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;

import butils.ByUtils;
import byransha.graph.BBGraph;
import byransha.graph.BNode;
import byransha.graph.NodeAction;
import byransha.graph.view.NodeView;
import byransha.nodes.system.SystemB;

public class ShellServer extends SystemB {
	@FunctionalInterface
	interface CommandAction {
		void exec(PrintWriter out) throws Throwable;
	}

	record Command(String description, CommandAction action) {
	}

	private final Map<String, Command> commands = new HashMap<>();

	public ShellServer(BBGraph g, int port) throws Throwable {
		super(g);
		System.out.println("Starting shell server on port " + port);
		initializeCommands(g);

		new Thread(() -> {
			try {
				startServer(port);
			} catch (Throwable err) {
				error(err);
			}
		}).start();
	}



	private void startServer(int port) throws Throwable {
		try (var serverSocket = new ServerSocket(port)) {
			System.out.println("Telnet server listening on port " + port);

			while (true) {
				try (var clientSocket = serverSocket.accept();
						var in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
						var out = new PrintWriter(clientSocket.getOutputStream(), true)) {

					String line = in.readLine();
					System.out.println("line: " + line);

					if (line == null)
						break;

					line = line.trim();

					if (line.isEmpty()) {
						out.println("Empty command received, ignoring");
					} else if (line.startsWith("auth ")) {
						var username = line.substring("auth ".length() + 1).trim();
						var userhash = username.hashCode();
						out.println(userhash);
					} else if (line.startsWith(".")) {
						showView(line.substring(1), out);
					} else if (line.startsWith("/")) {
						execLocalCommand(line.substring(1), out);
					} else {
						execAction(line, out);
					}

					clientSocket.getOutputStream().flush();
					clientSocket.close();
				} catch (IOException e) {
					System.err.println("Client error: " + e.getMessage());
				}
			}
		}
	}

	private void initializeCommands(BBGraph graph) {
		commands.put("help", new Command("list available commands",
				out -> commands.forEach((name, cmd) -> out.println(name + " - " + cmd.description))));

		commands.put("whoami",
				new Command("print current user", out -> out.println(graph.systemNode.getCurrentUser())));

		commands.put("pwd", new Command("print current node", out -> out.println(currentNode())));

		commands.put("kill", new Command("kill the server's JVM", out -> System.exit(0)));

		commands.put("actions", new Command("list actions available on this node",
				out -> currentNode().actions().forEach(a -> out.println(a.technicalName()))));

		commands.put("views", new Command("list views available on this node",
				out -> currentNode().views().forEach(v -> out.println(v.technicalName()))));

		commands.put("ls", new Command("list outs",
				out -> currentNode().forEachOut((name, node) -> out.println(name + ": " + node))));

		commands.put("lf",
				new Command("list fields", out -> currentNode().forEachOutField(f -> out.println(f.getName()))));

		commands.put("id", new Command("print current node ID", out -> out.println(currentNode().id())));

		commands.put("name", new Command("print current node name", out -> out.println(currentNode())));
	}

	private void execAction(String actionName, PrintWriter out) throws Throwable {
		var action = currentNode().findAction(actionName);

		if (action == null) {
			out.println(
					"no such action " + actionName + " on node " + currentNode() + " of " + currentNode().getClass());
		} else {
			var r = action.exec();

			for (var v : r.result.views()) {
				out.println(v.prettyName() + ":");
				out.println(v.toJSON().toPrettyString());
			}

			out.println("*" + action.prettyName() + " completed in " + ByUtils.ms2string( r.durationMs()) + "ms:");
		}
	}

	private void showView(String viewName, PrintWriter out) {
		var view = findView(currentNode(), viewName);

		if (view == null) {
			out.println("no such view " + viewName + " on node " + currentNode() + " of " + currentNode().getClass());
		} else {
			var r = view.toJSON();
			out.println(r.toPrettyString());
		}
	}

	private void execLocalCommand(String cmdName, PrintWriter out) {
		Command cmd = commands.get(cmdName);

		if (cmd != null) {
			try {
				cmd.action.exec(out);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		} else {
			out.println("Unknown command: " + cmdName);
		}
	}

	

	public static NodeView<BNode> findView(BNode n, String name) {
		for (var v : n.views()) {
			if (v.technicalName().equals(name)) {
				return v;
			}
		}

		return null;
	}

	private BNode currentNode() {
		return g.systemNode.getCurrentUser().currentNode();
	}

	@Override
	public String whatIsThis() {
		return "the TCP server for command line interaction with the graph";
	}

	@Override
	public String prettyName() {
		return "shell server";
	}
}