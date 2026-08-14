package byransha;

import java.util.HashMap;
import java.util.Map;

import byransha.access_control.User;
import byransha.network.TCPServer;
import byransha.service.system.Byransha;
import byransha.service.system.Hub;
import byransha.ui.swing.SwingFrontend;
import byransha.ui.telnet.TelnetServer;
import lab.LabApplication;

public class Main {

	public static void main(String... args) throws Throwable {
		System.out.println("This is Byransha v" + Byransha.VERSION);
		// System.out.println(args.length + " args: " + Arrays.toString(args));
		var argMap = mapArgs(args);
		System.out.println(argMap);
		Byransha.autoRestartWhenUpgraded = argMap.containsKey("--auto-restart");
		Byransha.autoUpdateEnabled = !argMap.containsKey("--disable-auto-update");

		if (Byransha.pathElements().length == 1) {// runFromASingleJar
			Byransha.considerUpgrading();
			Byransha.runAutoUpdateThread();
		} else {
			System.out.println("This is a development version, no upgrade possible");
		}

		int nbInstances = argMap.containsKey("--nb-instances") ? Integer.parseInt(argMap.get("--nb-instances")) : 1;

		for (int i = 0; i < nbInstances; ++i) {
			f(argMap, i);
		}

		Thread.sleep(Long.MAX_VALUE);

	}

	public static void f(Map<String, String> argMap, int i) throws Throwable {
		int tcpPort = i
				+ (argMap.containsKey("--port") ? Integer.parseInt(argMap.get("--port")) : TCPServer.DEFAULT_PORT);

		var hub = new Hub(tcpPort);
		hub.application = (Element) Class.forName(argMap.getOrDefault("appClass", LabApplication.class.getName()))
				.getConstructor(Element.class).newInstance(hub);

		new Chat(hub.currentUser()).append(hub.application);

		// new WebServer(g, Integer.parseInt(argMap.getOrDefault("--web-port",
		// "8080")));
		int telnetPort = i + Integer.parseInt(argMap.getOrDefault("--telnet-port", "" + TelnetServer.DEFAULT_PORT));
		new TelnetServer(hub, telnetPort);

		if (!argMap.containsKey("--no-gui")) {
			new SwingFrontend(hub);
		}

		System.out.println("playing events");
		hub.eventList.goToNow(e -> System.out.println("event: " + e));
		hub.setCurrentUser(new User(hub, "guest"));
		System.out.println("start ok");
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
