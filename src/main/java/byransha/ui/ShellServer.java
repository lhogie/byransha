package byransha.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.view.NodeView;
import byransha.nodes.system.ChatNode;
import byransha.nodes.system.SystemNode;
import byransha.util.ByUtils;

public class ShellServer extends SystemNode {
	@FunctionalInterface
	interface CommandAction {
		void exec(PrintWriter out, List<String> parms) throws Throwable;
	}

	record Command(String description, CommandAction action) {
	}

	private final Map<String, Command> commands = new HashMap<>();
	public ChatNode currentChat;

	public ShellServer(BGraph g, int port) throws Throwable {
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

	private void initializeCommands(BGraph graph) {
		commands.put("help", new Command("list available commands",
				(out, parms) -> commands.forEach((name, cmd) -> out.println(name + " - " + cmd.description))));

		commands.put("whoami", new Command("print current user", (out, parms) -> out.println(graph.getCurrentUser())));

		commands.put("pwd", new Command("print current node", (out, parms) -> out.println(currentNode())));

		commands.put("kill", new Command("kill the server's JVM", (out, parms) -> System.exit(0)));

		commands.put("actions", new Command("list actions available on this node",
				(out, parms) -> currentNode().actions().forEach(a -> out.println(a.technicalName()))));

		commands.put("views", new Command("list views available on this node",
				(out, parms) -> currentNode().views().forEach(v -> out.println(v.technicalName()))));

		commands.put("ls", new Command("list outs",
				(out, parms) -> currentNode().forEachOut((name, node) -> out.println(name + ": " + node))));

		commands.put("lf", new Command("list fields",
				(out, parms) -> currentNode().forEachOutInFields(currentNode().getClass(), BNode.class, (f, o, ro) -> out.println(f.getName()))));

		commands.put("id", new Command("print current node ID", (out, parms) -> out.println(currentNode().id())));

		commands.put("name", new Command("print current node name", (out, parms) -> out.println(currentNode())));
		commands.put("chat", new Command("print current chat ID", (out, parms) -> out.println(currentChat.id())));
		commands.put("chats", new Command("print available chats", (out, parms) -> out
				.println(currentChat.currentUser().chatList.elements.stream().map(c -> c.idAsText()).toList())));
		commands.put("newchat", new Command("create new chat",
				(out, parms) -> out.println(new ChatNode(currentUser(), currentChat.currentNode()).id())));
		commands.put("setcurrentchat", new Command("change chat",
				(out, parms) -> out.println(currentChat = (ChatNode) g.indexes.byId.getByText(parms.removeFirst()))));
		commands.put("deletechat",
				new Command("delete node", (out, parms) -> g.indexes.byId.getByText(parms.removeFirst()).delete()));
	}

	private void execAction(String actionName, PrintWriter out) throws Throwable {
		var action = currentNode().findAction(actionName);

		if (action == null) {
			out.println(
					"no such action " + actionName + " on node " + currentNode() + " of " + currentNode().getClass());
		} else {
			var r = action.exec(currentChat);

			for (var v : r.outNode.views()) {
				out.println(v.prettyName() + ":");
				out.println(v.toJSON().toPrettyString());
			}

			out.println("*" + action.prettyName() + " completed in " + ByUtils.ms2string(r.durationMs.get()) + "ms:");
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

	private void execLocalCommand(String cmdLine, PrintWriter out) {
		var l = new ArrayList<>(List.of(cmdLine.split(" +")));
		var cmdName = l.removeFirst();
		var cmd = commands.get(cmdName);

		if (cmd != null) {
			try {
				cmd.action.exec(out, l);
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
		return currentChat.currentNode();
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