package byransha.service.system;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
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

import byransha.Element;
import byransha.action.Category;
import byransha.action.base.ShowInKishanView;
import byransha.primitive.URLNode;
import byransha.thread.ThreadNode;
import byransha.util.ByUtils;

public class Byransha extends Element {
	public static Hub hub;

	@ShowInKishanView
	public static final String VERSION = "0.0.101";

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
	public final URLNode sourceRepoURL = new URLNode(this, null, "https://github.com/lhogie/byransha");

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

	public static boolean autoUpdateEnabled = true;

	public static boolean autoRestartWhenUpgraded = false;

	public Byransha(Hub g) {
		super(g, null);
	}

	public static String[] pathElements() {
		return System.getProperty("java.class.path").split(System.getProperty("path.separator"));
	}

	public static boolean devVersion() {
		return pathElements().length != 1;
	}

	public static String lastVersionOnline() throws MalformedURLException, IOException {
		String jsonString = new String(downloadFromI3S("bin/info.json"));
		return ByUtils.objectMapper.readTree(jsonString).get("version").asText();
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

	public static void runAutoUpdateThread() {
		System.out.println("Running auto update thread");
		ThreadNode.thread("check new version", () -> {
			while (true) {
				try {
					Thread.sleep(10000);
					considerUpgrading();
				} catch (InterruptedException err) {
					err.printStackTrace();
				}
			}
		});
	}

	public static void considerUpgrading() {
		try {
			if (autoUpdateEnabled && !lastVersionOnline().equals(Byransha.VERSION)) {
				System.out.println("downloading last version to " + jarFile);
				var lastVersion = Byransha.downloadLastVersion();
				System.out.println("overwriting " + jarFile);
				Files.write(jarFile.toPath(), lastVersion, StandardOpenOption.TRUNCATE_EXISTING);

				if (!jarFile.equals(installedJarFile)) {
					install();
				}

				if (hub != null && hub.swingInterface != null) {
					JOptionPane.showMessageDialog(hub.swingInterface.frame, "A new version was downloaded",
							"Restart required", JOptionPane.INFORMATION_MESSAGE);
				}

				if (autoRestartWhenUpgraded) {
					System.out.println("upgraded. quitting...");
					System.exit(0);
				}
			}
		} catch (IOException | InterruptedException err) {
			err.printStackTrace();
		}
	}

	private static void install() throws IOException, InterruptedException {
		System.out.println("installing to " + Byransha.binDirectory);
		installedJarFile.getParentFile().mkdirs();
		ByUtils.extractResource("/systemD_service/byransha.service", Byransha.homeDirectory);
		ByUtils.extractResource("/systemD_service/create.sh", Byransha.homeDirectory);
		ByUtils.extractResource("/systemD_service/delete.sh", Byransha.homeDirectory);

		Files.copy(jarFile.toPath(), installedJarFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		jarFile.delete();

		if (ByUtils.isWindows()) {
			createDesktopShortcut(installedJarFile.toPath(), "Byransha");
		} else {
			// Files.write(new File(System.getProperty("user.home")).toPath(), "java -jar
			// $HOME/.local/share/byransha/bin/byransha.jar --no-gui".getBytes(),
			// StandardOpenOption.TRUNCATE_EXISTING);
		}

		runVersionSpecificMigrationCode();
	}

	private static void runVersionSpecificMigrationCode() {
		// maybe be needed to do changes on the file system that will be needed for the
		// next version
	}

	public static File createDesktopShortcut(Path jarPath, String appName) throws IOException, InterruptedException {

		File absoluteJar = jarPath.toAbsolutePath().normalize().toFile();
		File workingDir = absoluteJar.getParentFile();

		// 1. Resolve Desktop path
		String userProfile = System.getenv("USERPROFILE");
		File desktopDir = new File(userProfile, "Desktop");
		File shortcutFile = new File(desktopDir, appName + ".lnk");

		// 2. Locate javaw.exe from the current running JVM
		String javaHome = System.getProperty("java.home");
		File javawExe = new File(javaHome, "bin" + File.separator + "javaw.exe");

		// Fallback to java.exe if javaw.exe isn't found
		String targetExe = javawExe.exists() ? javawExe.getAbsolutePath() : "javaw.exe";

		// 3. Arguments passed to javaw.exe
		String arguments = "-jar \"" + absoluteJar.getAbsolutePath() + "\"";

		// 4. Build PowerShell command to create the .lnk file
		String psCommand = String.format(
				"$WScript = New-Object -ComObject WScript.Shell; " + "$Shortcut = $WScript.CreateShortcut('%s'); "
						+ "$Shortcut.TargetPath = '%s'; " + "$Shortcut.Arguments = '%s'; "
						+ "$Shortcut.WorkingDirectory = '%s'; " + "$Shortcut.Save();",
				shortcutFile.getAbsolutePath().replace("'", "''"), targetExe.replace("'", "''"),
				arguments.replace("'", "''"), workingDir.getAbsolutePath().replace("'", "''"));

		// 5. Execute PowerShell process
		ProcessBuilder pb = new ProcessBuilder("powershell.exe", "-NoProfile", "-NonInteractive", "-Command",
				psCommand);
		Process process = pb.start();
		int exitCode = process.waitFor();

		if (exitCode != 0) {
			throw new IOException("Failed to create shortcut via PowerShell (Exit code: " + exitCode + ")");
		}

		return shortcutFile;
	}

}
