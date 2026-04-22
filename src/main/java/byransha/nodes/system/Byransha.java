package byransha.nodes.system;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;

import byransha.graph.BGraph;
import byransha.graph.ShowInKishanView;
import byransha.nodes.primitive.StringNode;
import byransha.nodes.primitive.URLNode;
import byransha.util.ByUtils;
import byransha.util.Version;

public class Byransha extends SystemNode {
	public final StringNode currentVersionNode = new StringNode(this, VERSION.toString(), ".*");
	public final StringNode remoteVersionNode = new StringNode(this, null, ".*");
	public final URLNode sourceRepoURL = new URLNode(this, "https://github.com/lhogie/byransha");

	@ShowInKishanView
	public static final File configDirectory = new File(ByUtils.home, ".byransha");
	public static final Version VERSION = new Version("0.0.1");
	public static final File exeFile = new File(configDirectory, "/byransha.jar");
	public static final File lastVersionFile = new File(configDirectory, "/last-version.txt");
	public static final String homepage = "https://webusers.i3s.unice.fr/~hogie/software/byransha/";
	public static final String downloads = "https://webusers.i3s.unice.fr/~hogie/software/byransha/";
	public static final String downloadBinaries = downloads + "bin/";
	public static final String lastVersionURL = downloadBinaries + "last-version.txt";
	public static byte[] currentExeBytes = "".getBytes();

	public Byransha(BGraph g) {
		super(g);

		// if production version (only 1 JAR in classpath
		// try to update it periodically
		if (System.getProperty("java.class.path").equals(exeFile.getAbsolutePath())) {
			new Thread(() -> {
				while (true) {
					try {
						var lastV = lastVersionOnline();

						if (lastV.isNewerThan(VERSION)) {
							var exeBytes = download(VERSION);
							Files.write(exeFile.toPath(), exeBytes);
							Files.write(lastVersionFile.toPath(), lastV.toString().getBytes());
							currentVersionNode.set(lastV.toString());
						}

						Thread.sleep(10000);
					} catch (IOException | InterruptedException e) {
						g().errorLog.add(e);
					}
				}
			}, "check new version thread");// .start();
		}
	}

	public Version lastVersionOnline() throws MalformedURLException, IOException {
		return new Version(new String(new URL(lastVersionURL).openStream().readAllBytes()));
	}

	public byte[] download(Version v) throws MalformedURLException, IOException {
		return new URL(downloadBinaries + "/byransha-" + v).openStream().readAllBytes();
	}

	@Override
	public void createActions() {
		cachedActions.elements.add(new Restart(this));
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
