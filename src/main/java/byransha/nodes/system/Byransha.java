package byransha.nodes.system;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.swing.JOptionPane;

import com.fasterxml.jackson.databind.JsonNode;

import byransha.graph.BGraph;
import byransha.graph.Category;
import byransha.graph.ShowInKishanView;
import byransha.nodes.primitive.URLNode;
import byransha.util.ByUtils;

public class Byransha extends SystemNode {
	@ShowInKishanView
	public static final String VERSION = "0.0.32";

	public static class byransha extends Category {
	}

	@ShowInKishanView
	public static final File jarFile;

	@ShowInKishanView
	private static boolean runFromASingleJar;

	static {
		var classPath = pathElements();
		runFromASingleJar = classPath.length == 1;
		jarFile = runFromASingleJar ? new File(classPath[0]).getAbsoluteFile() : null;
	}

	@ShowInKishanView
	public final URLNode sourceRepoURL = new URLNode(this, "https://github.com/lhogie/byransha");

	@ShowInKishanView
	public static final File homeDirectory = new File(System.getProperty("user.home")
			+ (ByUtils.isWindows() ? "/AppData/Local/byransha" : "/.local/share/byransha"));
	@ShowInKishanView
	public static final File binDirectory = new File(homeDirectory, "bin");
	@ShowInKishanView
	public static final String homepage = "https://webusers.i3s.unice.fr/~hogie/software/byransha/";
	public static final String downloads = homepage + "/downloads/";
	public static final String downloadBinaries = downloads + "bin/";
	public static final String lastVersionURL = downloadBinaries + "info.json";
	public static File installedJarFile = new File(binDirectory, "byransha.jar");

	public Byransha(BGraph g) {
		super(g);

		new Thread(() -> {
			while (true) {
				try {
					String versionOnline = lastVersionOnline();

					if (!versionOnline.equals(VERSION)) {
						System.out.println("New version available: " + versionOnline);
					}
				} catch (IOException e) {
					System.err.println("no internet");
				}

				sleep(10);
			}
		}, "check new version thread");// .start();
	}

	public static String[] pathElements() {
		return System.getProperty("java.class.path").split(System.getProperty("path.separator"));
	}

	public static boolean devVersion() {
		return pathElements().length != 1;
	}

	public static String lastVersionOnline() throws MalformedURLException, IOException {
		String jsonString = new String(downloadFromI3S("bin/info.json"));
		JsonNode rootNode = objectMapper.readTree(jsonString);
		return rootNode.get("version").asText();
	}

	public static byte[] downloadLastVersion() throws MalformedURLException, IOException {
		return downloadFromI3S("bin/byransha.jar");
	}

	public static byte[] downloadFromI3S(String filename) throws MalformedURLException, IOException {
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(X509Certificate[] certs, String authType) {
			}
		} };
		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
			return new URL(downloads + "/" + filename).openStream().readAllBytes();
		} catch (NoSuchAlgorithmException | KeyManagementException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void createActions() {
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

	public static void runAutoUpdateThread(Component c) {
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

						if (c != null) {
							JOptionPane.showMessageDialog(c,
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

	public static boolean upgradeIfNecessary() throws MalformedURLException, IOException {
		if (!lastVersionOnline().equals(Byransha.VERSION)) {
			System.out.println("upgrading " + jarFile);
			Files.write(jarFile.toPath(), Byransha.downloadLastVersion(), StandardOpenOption.TRUNCATE_EXISTING);
			return true;
		} else {
			return false;
		}
	}

	public static void install() throws IOException, InterruptedException {
		System.out.println("installing to " + Byransha.binDirectory);
		installedJarFile.getParentFile().mkdirs();
		ByUtils.extractResource("/systemD_service/byransha.service", Byransha.homeDirectory);
		ByUtils.extractResource("/systemD_service/create.sh", Byransha.homeDirectory);
		ByUtils.extractResource("/systemD_service/delete.sh", Byransha.homeDirectory);

		Files.copy(jarFile.toPath(), installedJarFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		jarFile.delete();

		if (ByUtils.isWindows()) {
			var link = ByUtils.windowsMenuLink(installedJarFile.toPath(), "Byransha");

			if (link.exists()) {
				link.delete();
			}

			ByUtils.createShortcutViaPowerShell(installedJarFile.toPath(), link);
		} else {
			// Files.write(new File(System.getProperty("user.home")).toPath(), "java -jar
			// $HOME/.local/share/byransha/bin/byransha.jar --no-gui".getBytes(),
			// StandardOpenOption.TRUNCATE_EXISTING);
		}
	}
}
