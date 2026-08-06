package byransha;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import byransha.event.Event;
import byransha.graph.BNode;
import byransha.graph.Hub;
import byransha.lab.I3S;
import byransha.lab.Person;
import byransha.network.TCPServer;
import byransha.system.Byransha;
import byransha.system.ChatNode;
import byransha.system.User;
import byransha.ui.shell.ShellServer;
import byransha.ui.swing.SwingFrontend;

public class Main {
	public static Hub hub;

	public static void main(String... args) throws Throwable {
		System.out.println("This is Byransha v" + Byransha.VERSION);
//		System.out.println(args.length + " args: " + Arrays.toString(args));
		var argMap = mapArgs(args);
		Byransha.autoRestart = argMap.containsKey("--auto-restart");
		Byransha.autoUpdateEnabled = !argMap.containsKey("--disable-auto-update");

		var classPath = Byransha.pathElements();
		boolean runFromASingleJar = classPath.length == 1;

		if (runFromASingleJar) {
			try {
				if (Byransha.autoUpdateEnabled) {
					Byransha.upgradeIfNecessary();

					if (!Byransha.jarFile.equals(Byransha.installedJarFile)) {
						Byransha.install();
					}
				}
			} catch (IOException err) {
				System.err.println("no internet");
				err.printStackTrace();
			}

			Byransha.runAutoUpdateThread();
		} else {
			System.out.println("Development version using: " + Arrays.toString(classPath));
		}

		int port = argMap.containsKey("--port") ? Integer.parseInt(argMap.get("--port")) : TCPServer.DEFAULT_PORT;

		hub = new Hub(port);
		hub.application = (BNode) Class.forName(argMap.getOrDefault("appClass", I3S.class.getName()))
				.getConstructor(BNode.class).newInstance(hub);

		new ChatNode(hub.currentUser()).append(hub.application);

		// new WebServer(g, Integer.parseInt(argMap.getOrDefault("--web-port",
		// "8080")));
		new ShellServer(hub, Integer.parseInt(argMap.getOrDefault("--telnet-port", "" + ShellServer.DEFAULT_PORT)));

		if (!argMap.containsKey("--no-gui")) {
			new SwingFrontend(hub);
		}

		System.out.println("playing events");
		hub.eventList.goToNow(e -> System.out.println("event: " + e));
		hub.setCurrentUser(new User(hub, "guest"));
		System.out.println("start ok");

		Thread.sleep(Long.MAX_VALUE);

		// Dotenv dotenv = Dotenv.load();

		// // Récupère la valeur
		// String serverName = dotenv.get("PUBLIC_SERVER_NAME");

		// if ((InetAddress.getLocalHost().getHostName().equals(serverName))) {
		// System.out.println("AI is used on the server");
		// QueryIA.startOllama();
		// }
		// else {
		// System.out.println("AI is not used on the server");
		// System.out.println("name: " + InetAddress.getLocalHost().getHostName());
		// System.out.println("PUBLIC_SERVER_NAME: " + serverName);
		// }
	}

	private static Event createPersonEvent(String name) {
		var e = new NewNodeEvent<Person>(hub, LocalDateTime.now());
		e.clazz = Person.class;
		return e;
	}

	private static Map<String, String> mapArgs(String... args) {
		var r = new HashMap<String, String>();

		for (var arg : args) {
			if (arg.contains("=")) {
				var a = arg.split("=");
				r.put(a[0], a[1]);
			} else {
				r.put(arg, "");
			}
		}

		return r;
	}
}
