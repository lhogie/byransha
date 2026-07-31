package byransha;
import java.io.File;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import byransha.ai.QueryIA;
import byransha.event.Event;
import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.network.TCPDriver;
import byransha.nodes.lab.I3S;
import byransha.nodes.lab.Person;
import byransha.nodes.system.ChatNode;
import byransha.nodes.system.User;
import byransha.ui.swing.SwingFrontend;
import io.github.cdimascio.dotenv.Dotenv;

public class Main {
	static BGraph g;

	public static void main(String[] args) throws Throwable {
		// System.out.println("IA".split("/").length);

		// java.awt.Toolkit.getDefaultToolkit();
		// Application.setUserAgentStylesheet(new
		// PrimerDark().getUserAgentStylesheet());
		var argMap = mapArgs(args);

		int port = argMap.containsKey("--port") ? Integer.parseInt(argMap.get("--port")) : TCPDriver.DEFAULT_PORT;

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

		// new JavaFXFrontend(g);

		System.out.println("playing events");
		g.eventList.goToNow(e -> System.out.println("event: " + e));
		g.setCurrentUser(new User(g, "guest"));
		System.out.println("start ok");


		Dotenv dotenv = Dotenv.load();

        // Récupère la valeur
        String serverName = dotenv.get("PUBLIC_SERVER_NAME");

		if ((InetAddress.getLocalHost().getHostName().equals(serverName))) {
			System.out.println("AI is used on the server");
			QueryIA.startOllama();
	}
	else {
		System.out.println("AI is not used on the server");
		System.out.println("name: " + InetAddress.getLocalHost().getHostName());
		System.out.println("PUBLIC_SERVER_NAME: " + serverName);
		}
	}


	private static Event createPersonEvent(String name) {
		var e = new NewNodeEvent<Person>(g, LocalDateTime.now());
		e.clazz = Person.class;
		return e;
	}

	private static Map<String, String> mapArgs(String... args) {
		var r = new HashMap<String, String>();

		for (var arg : List.of(args)) {
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

