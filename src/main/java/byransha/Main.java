package byransha;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.File;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import byransha.ai.OllamaModel;
import byransha.event.Event;
import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.nodes.lab.I3S;
import byransha.nodes.lab.Person;
import byransha.nodes.system.ChatNode;
import byransha.nodes.system.User;
import byransha.ui.shell.ShellServer;
import byransha.ui.swing.SwingFrontend;

public class Main {
	static BGraph g;

	public static void main(String[] args) throws Throwable {
		OllamaModel.initialModel(null);

//		System.out.println("IA".split("/").length);

		// java.awt.Toolkit.getDefaultToolkit();
//		Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
		var argMap = mapArgs(args);

		File d = new File(argMap.getOrDefault("-directory", System.getProperty("user.home") + "/.byransha/"));
		g = new BGraph(d);
		g.application = (BNode) Class.forName(argMap.getOrDefault("appClass", I3S.class.getName()))
				.getConstructor(BNode.class).newInstance(g);

		new ChatNode(g.currentUser).append(g.application);

//		new WebServer(g, Integer.parseInt(argMap.getOrDefault("--web-port", "8080")));
		new ShellServer(g, Integer.parseInt(argMap.getOrDefault("--telnet-port", "" + ShellServer.DEFAULT_PORT)));
		new SwingFrontend(g);
		// new JavaFXFrontend(g);

		System.out.println("playing events");
		g.eventList.goToNow(e -> System.out.println("event: " + e));
		g.currentUser = new User(g, "guest");

		// launch(args);
	}

	private static Event createPersonEvent(String name) {
		var e = new NewNodeEvent<Person>(g, LocalDateTime.now());
		e.clazz = Person.class;
		return e;
	}

	private static Map<String, String> mapArgs(String... args) {
		var r = new HashMap<String, String>();
		List.of(args).stream().map(a -> a.split("=")).forEach(a -> r.put(a[0], a[1]));
		return r;
	}
}
