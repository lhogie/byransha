package byransha;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

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
import byransha.util.ByUtils;

public class Main {
	static BGraph g;

	public static void main(String... args) throws Throwable {
		System.out.println("This is Byransha v" + Byransha.VERSION);
		System.out.println(args.length + " args: " + Arrays.toString(args));

		ByUtils.extractResource("/systemD_service/byransha.service", Byransha.homeDirectory);
		ByUtils.extractResource("/systemD_service/create.sh", Byransha.homeDirectory);
		ByUtils.extractResource("/systemD_service/delete.sh", Byransha.homeDirectory);

		var classPath = Byransha.pathElements();
		boolean runFromASingleJar = classPath.length == 1;

		if (runFromASingleJar) {
			var jarFile = new File(classPath[0]);

			try {
				if (!Byransha.lastVersionOnline().equals(Byransha.VERSION)) {
					System.out.println("upgrading " + jarFile);
					Files.write(jarFile.toPath(), Byransha.downloadLastVersion(), StandardOpenOption.TRUNCATE_EXISTING);
				}

				File installedJar = Byransha.getInstalledJarFile();
				installedJar.getParentFile().mkdirs();

				if (!jarFile.equals(installedJar)) {
					System.out.println("moving " + jarFile + " to " + installedJar.getParentFile());
					Files.copy(jarFile.toPath(), installedJar.toPath(), StandardCopyOption.REPLACE_EXISTING);
					jarFile.delete();

					if (ByUtils.isWindows()) {
						var link = ByUtils.windowsMenuLink(installedJar.toPath(), "Byransha");

						if (link.exists()) {
							link.delete();
						}

						ByUtils.createShortcutViaPowerShell(installedJar.toPath(), link);
					} else {
						// Files.write(new File(System.getProperty("user.home")).toPath(), "java -jar
						// $HOME/.local/share/byransha/bin/byransha.jar --no-gui".getBytes(),
						// StandardOpenOption.TRUNCATE_EXISTING);
					}
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
			var jarFile = new File(classPath[0]);

			new Thread(() -> {
				while (true) {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					try {
						if (!Byransha.lastVersionOnline().equals(Byransha.VERSION)) {
							System.out.println("upgrading " + jarFile);
							Files.write(jarFile.toPath(), Byransha.downloadLastVersion(),
									StandardOpenOption.TRUNCATE_EXISTING);
							if (g.swing != null) {
								JOptionPane.showMessageDialog(g.swing.frame,
										"A new version was downloaded and installed, you must restart the application",
										"Restart requireed", JOptionPane.INFORMATION_MESSAGE);
							}

							System.out.println("quitting");
							System.exit(0);
						}

					} catch (IOException err) {
						System.err.println("no internet");
						err.printStackTrace();
					}
				}
			}).start();
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
