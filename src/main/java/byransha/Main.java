package byransha;

import java.util.HashMap;
import java.util.Map;

import byransha.graph.BNode;
import byransha.graph.Hub;
import byransha.lab.I3S;
import byransha.network.TCPServer;
import byransha.system.Byransha;
import byransha.system.ChatNode;
import byransha.system.User;
import byransha.ui.shell.ShellServer;
import byransha.ui.swing.SwingFrontend;

public class Main {

	public static void main(String... args) throws Throwable {
		System.out.println("This is Byransha v" + Byransha.VERSION);
//		System.out.println(args.length + " args: " + Arrays.toString(args));
		var argMap = mapArgs(args);
		Byransha.autoRestartWhenUpgraded = argMap.containsKey("--auto-restart");
		Byransha.autoUpdateEnabled = !argMap.containsKey("--disable-auto-update");

		if (Byransha.pathElements().length == 1) {// runFromASingleJar
			Byransha.considerUpgrading();
			Byransha.runAutoUpdateThread();
		} else {
			System.out.println("This is a development version, no upgrade possible");
		}

		int port = argMap.containsKey("--port") ? Integer.parseInt(argMap.get("--port")) : TCPServer.DEFAULT_PORT;

		var hub =  new Hub(port);
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
