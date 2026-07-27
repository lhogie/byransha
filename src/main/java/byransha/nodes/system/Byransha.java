package byransha.nodes.system;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.fasterxml.jackson.databind.JsonNode;

import byransha.VersionNode;
import byransha.graph.BGraph;
import byransha.graph.Category;
import byransha.graph.ShowInKishanView;
import byransha.nodes.primitive.URLNode;
import byransha.util.ByUtils;

public class Byransha extends SystemNode {
	public static final String VERSION = "0.0.24";

	public static class byransha extends Category {
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

	@ShowInKishanView
	public final VersionNode version = new VersionNode(this);

	public Byransha(BGraph g) {
		super(g);

		new Thread(() -> {
			while (true) {
				try {
					String versionOnline = lastVersionOnline();

					if (!versionOnline.equals(version.version.toString())) {
						System.out.println("New version available: " + versionOnline);
					}
				} catch (IOException e) {
					System.err.println("no internet");
				}

				sleep(10000);
			}
		}, "check new version thread");// .start();
	}

	public static File getInstalledJarFile() {
		return new File(binDirectory, "byransha.jar");
	}

	public static String[] pathElements() {
		return System.getProperty("java.class.path").split(System.getProperty("path.separator"));
	}

	public static boolean devVersion() {
		return pathElements().length != 1;
	}

	public static String lastVersionOnline() throws MalformedURLException, IOException {
		String jsonString = new String(downloadFromI3S("bin/info.json"));

		try {
			JsonNode rootNode = objectMapper.readTree(jsonString);
			return rootNode.get("version").asText();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
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
}
