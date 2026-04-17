package byransha.nodes.system;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;

import byransha.graph.BGraph;
import byransha.nodes.primitive.StringNode;
import byransha.nodes.primitive.URLNode;
import byransha.util.ByUtils;

public class Byransha extends SystemNode {
	public final StringNode versionNode;
	public final URLNode sourceRepoURL;
	public final File configDirectory;
	public final StringNode exeFile;
	public static final String VERSION = "0.0.1";
	public static final String downloadExeURL = "https://webusers.i3s.unice.fr/~hogie/software/byransha/";
	public static byte[] currentExeBytes = "".getBytes();

	public Byransha(BGraph g) {
		super(g);
		exeFile = new StringNode(g);
		var classpath = System.getProperty("java.class.path").split(File.pathSeparator);

		if (classpath.length == 1) {
			exeFile.set(classpath[0]);
		} else {
			exeFile.set("development classpath");
		}

		versionNode = new StringNode(g, VERSION, ".*");
		sourceRepoURL = new URLNode(g, "https://github.com/lhogie/byransha");
		configDirectory = new File(ByUtils.home, ".byransha");
		var exeFile = new File(configDirectory, "byransha.jar");

		try {
			currentExeBytes = exeFile.exists() ? Files.readAllBytes(exeFile.toPath()) : "".getBytes();

			new Thread(() -> {
				while (true) {
					try {
						var exeBytes = new URL(downloadExeURL).openStream().readAllBytes();

						if (!Arrays.equals(currentExeBytes, exeBytes)) {
							Files.write(exeFile.toPath(), exeBytes);
							currentExeBytes = exeBytes;
							versionNode.set(versionNode.get() + " - new version installed");
						}

						Thread.sleep(60000);
					} catch (IOException | InterruptedException e) {
						error(e);
					}
				}

			}, "check new version thread");//.start();

		} catch (IOException err) {
			error(err);
		}
	}

	@Override
	public void createActions() {
		cachedActions.elements.add(new Restart( this));
		super.createActions();
	}

	@Override
	public String toString() {
		return "Byransha";
	}

	@Override
	public String whatIsThis() {
		return "Byransha";
	}

}
