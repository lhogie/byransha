package byransha.nodes.system;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import byransha.graph.BGraph;
import byransha.graph.ShowInKishanView;
import byransha.nodes.primitive.StringNode;
import byransha.nodes.primitive.URLNode;
import byransha.util.ByUtils;
import byransha.util.Version;

public class Byransha extends SystemNode {
	public final StringNode remoteVersionNode = new StringNode(this, null, ".*");
	@ShowInKishanView
	public final URLNode sourceRepoURL = new URLNode(this, "https://github.com/lhogie/byransha");

	@ShowInKishanView
	public static final File configDirectory = new File(ByUtils.home, ".byransha");
	@ShowInKishanView
	public static final File binDirectory = new File(configDirectory, "/bin/");
	@ShowInKishanView
	public static final File jarDirectory = new File(binDirectory, "/jar/");
	public static final File lastVersionFile = new File(configDirectory, "/last-version.txt");
	@ShowInKishanView
	public static final String homepage = "https://webusers.i3s.unice.fr/~hogie/software/byransha/";
	public static final String downloads = "https://webusers.i3s.unice.fr/~hogie/software/byransha/downloads/";
	public static final String downloadBinaries = downloads + "bin/";
	public static final String lastVersionURL = downloadBinaries + "last-version.txt";
	public static byte[] currentExeBytes = "".getBytes();
	public final StringNode versionNode = new StringNode(this, "0.0.1", "[0-9]+\\.[0-9]+\\.[0-9]+");
	public final StringNode scpHost = new StringNode(this, "bastion.i3s.unice.fr", ".+");
	public final StringNode scpRemoteDir = new StringNode(this, "public_html/software/byransha/downloads/",
			".+");

	public Byransha(BGraph g) {
		super(g);

		new Thread(() -> {
			while (true) {
				try {
					var versionOnline = lastVersionOnline();
					Version localVersion = new Version(versionNode.get());

					if (versionOnline.isNewerThan(localVersion)) {
						var zip = download(versionOnline);
						Arrays.stream(jarDirectory.listFiles()).forEach(jar -> jar.delete());
						extractZipByteArray(zip, jarDirectory);
						Files.write(lastVersionFile.toPath(), versionOnline.toString().getBytes());
						versionNode.set(versionOnline.toString());
					}

					Thread.sleep(10000);
				} catch (IOException | InterruptedException e) {
					g().errorLog.add(e);
				}
			}
		}, "check new version thread");// .start();
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
		cachedActions.elements.add(new Deploy(this));
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

	public static void extractZipByteArray(byte[] zipData, File to) throws IOException {
		to.mkdirs();

		try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipData))) {
			ZipEntry entry;

			while ((entry = zis.getNextEntry()) != null) {
				File f = new File(to, entry.getName());
				Files.copy(zis, to.toPath(), StandardCopyOption.REPLACE_EXISTING);
			}

			zis.closeEntry();
		}
	}

}
