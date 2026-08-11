package byransha.ui.shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import byransha.ID;
import byransha.graph.Element;
import byransha.graph.Hub;
import byransha.graph.list.action.FunctionAction;
import byransha.util.ByUtils;

public class TelnetSession extends Element {

	public Element currentNode = hub();

	@FunctionalInterface
	interface CommandAction {
		void exec(PrintWriter out, List<String> parms) throws Throwable;
	}

	record Command(String description, CommandAction action) {
	}

	private final Map<String, Command> commands = new HashMap<>();
	private Socket socket;

	public TelnetSession(Socket clientSocket, ShellServer g) throws IOException {
		super(g, null);
		this.socket = clientSocket;
		var in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		var out = new PrintWriter(clientSocket.getOutputStream(), true);

		if (clientSocket.getInetAddress().isLoopbackAddress()) {
			initializeCommands(hub());

			new Thread(() -> {
				try {
					while (true) {
						out.write("byransha> ");
						out.flush();
						String line = in.readLine();

						if (line == null)
							break;

						line = line.trim();

						if (line.isEmpty()) {
						} else if (line.startsWith("auth ")) {
							var username = line.substring("auth ".length() + 1).trim();
							var userhash = username.hashCode();
							out.println(userhash);
						} else if (line.startsWith(".")) {
							execAction(line.substring(1), out);
						} else {
							execLocalCommand(line, out);
						}
					}
				} catch (Throwable err) {
					hub().errorLog.add(err);
				}

				try {
					clientSocket.getOutputStream().flush();
					clientSocket.close();
				} catch (IOException e) {
				}
			}).start();
		} else {
			String asciiArt = """
					      .-.
					      | |
					      | |
					      | |
					  .-.-| |.-.
					 /  | | |  \\
					|  | | | |  |
					|  | | | |  |
					|  |_|_|_|  |
					 \\         /
					  |       |
					  |       |
					""";
			out.println(asciiArt);
			out.flush();
			socket.close();
		}
	}

	private void initializeCommands(Hub graph) {
		commands.put("help", new Command("list available commands",
				(out, parms) -> commands.forEach((name, cmd) -> out.println(name + " - " + cmd.description))));

		commands.put("whoami", new Command("print current user", (out, parms) -> out.println(graph.getCurrentUser())));

		commands.put("pwd", new Command("print current node", (out, parms) -> out.println(currentNode)));

		commands.put("kill", new Command("kill the server's JVM", (out, parms) -> System.exit(0)));

		commands.put("actions", new Command("list actions available on this node",
				(out, parms) -> currentNode.actions().forEach(a -> out.println(a.technicalName()))));

		commands.put("ls", new Command("list outs", (out, parms) -> currentNode.forEachOut((node, name) -> out
				.println(node.getClass().getName() + " " + name + " = " + node + ", id: " + node.id()))));

		commands.put("goto", new Command("go to a specific node", (out, parms) -> {
			var target = ID.fromBase62(parms.getFirst());
			var n = hub().indexes.byId.get(target);

			if (n != null) {
				currentNode = n;
			} else {
				out.println("can't find node " + target);
			}
		}));

		commands.put("lf", new Command("list fields", (out, parms) -> currentNode
				.forEachOutInFields(currentNode.getClass(), Element.class, (f, o, ro) -> out.println(f.getName()))));

		commands.put("id", new Command("print current node ID", (out, parms) -> out.println(currentNode.id())));

		commands.put("name", new Command("print current node name", (out, parms) -> out.println(currentNode)));
	}

	private void execAction(String actionName, PrintWriter out) throws Throwable {
		var action = currentNode.findAction(actionName);

		if (action == null) {
			out.println("no such action " + actionName + " on node " + currentNode + " of " + currentNode.getClass());
		} else {
			action.execSync();

			if (action instanceof FunctionAction fa) {
				out.println(fa.result.describeAsJSON().toPrettyString());
			}

			out.println("*" + action + " completed in " + ByUtils.ms2string(action.durationMs.get()) + "ms:");
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
			out.println("! unknown command: " + cmdName);
		}
	}

	@Override
	public String toString() {
		return socket.getInetAddress().getHostName() + ":" + socket.getPort();
	}
}
