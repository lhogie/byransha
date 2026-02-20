package byransha;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import byransha.graph.BBGraph;
import byransha.graph.BNode;
import byransha.nodes.lab.I3S;
import byransha.web.WebServer;

public class Main {

	public static void main(String[] args) throws Throwable {
		var argMap = mapArgs(args);

		File d = new File(argMap.getOrDefault("-directory", System.getProperty("user.home") + "/.byransha/"));
		var g = new BBGraph(d);
		g.systemNode.application = (BNode) Class.forName(argMap.getOrDefault("appClass", I3S.class.getName()))
				.getConstructor(BBGraph.class).newInstance(g);

		new WebServer(g, Integer.parseInt(argMap.getOrDefault("--web-port", "8080")));
		new SwingFrontend(g);
		new ShellServer(g, Integer.parseInt(argMap.getOrDefault("--telnet-port", "1000")));

		g.systemNode.eventList.add(new CreateNewPerson("Luc"));
		g.systemNode.eventList.add(new CreateNewPerson("Dylan"));
		g.systemNode.eventList.add(new CreateNewPerson("Sophie"));

		System.out.println("playing events");
		g.systemNode.eventList.goToNow(e -> System.out.println("event: " + e));
	}

	private static Map<String, String> mapArgs(String... args) {
		var r = new HashMap<String, String>();
		List.of(args).stream().map(a -> a.split("=")).forEach(a -> r.put(a[0], a[1]));
		return r;
	}

}
