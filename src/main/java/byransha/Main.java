package byransha;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import byransha.event.Event;
import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.network.NetworkAgent;
import byransha.nodes.lab.I3S;
import byransha.nodes.lab.Person;
import byransha.nodes.system.Byransha;
import byransha.nodes.system.ChatNode;
import byransha.nodes.system.User;
import byransha.ui.swing.SwingFrontend;

public class Main {
	static BGraph g;

	public static void main(String... args) throws Throwable {
		System.out.println("This is Byransha v" + Byransha.VERSION);
		System.out.println(args.length + " args: " + Arrays.toString(args));

		var classPath = Byransha.pathElements();
		boolean runFromASingleJar = classPath.length == 1;

		if (runFromASingleJar) {
			try {
				Byransha.upgradeIfNecessary();

				if (!Byransha.jarFile.equals(Byransha.installedJarFile)) {
					Byransha.install();
				}
			} catch (IOException err) {
				System.err.println("no internet");
				err.printStackTrace();
			}
		} else {
			System.out.println("Development version using: " + classPath);
		}

		var argMap = mapArgs(args);

		int port = argMap.containsKey("--port") ? Integer.parseInt(argMap.get("--port")) : NetworkAgent.DEFAULT_PORT;

		File d = new File(argMap.getOrDefault("-directory", System.getProperty("user.home") + "/.byransha/"));
		g = new BGraph(d, port);
		g.application = (BNode) Class.forName(argMap.getOrDefault("appClass", I3S.class.getName()))
				.getConstructor(BNode.class).newInstance(g);

		new ChatNode(g.currentUser()).append(g.application);

		// new WebServer(g, Integer.parseInt(argMap.getOrDefault("--web-port",
		// "8080")));
		// new ShellServer(g, Integer.parseInt(argMap.getOrDefault("--telnet-port", "" +
		// ShellServer.DEFAULT_PORT)));

		if (!argMap.containsKey("--no-gui")) {
			new SwingFrontend(g);
		}

		System.out.println("playing events");
		g.eventList.goToNow(e -> System.out.println("event: " + e));
		g.setCurrentUser(new User(g, "guest"));
		System.out.println("start ok");

		if (runFromASingleJar) {
			Byransha.runAutoUpdateThread(g.swing != null ? g.swing.frame : null);
		}

		Thread.sleep(Long.MAX_VALUE);
	}

	private static Event createPersonEvent(String name) {
		var e = new NewNodeEvent<Person>(g, LocalDateTime.now());
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
