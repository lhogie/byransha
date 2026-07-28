package byransha.network;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.regex.Pattern;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.ShowInKishanView;

public class PeerNode extends BNode {
	@ShowInKishanView
	public String name;

	@ShowInKishanView
	public PublicKey publicKey;

	@ShowInKishanView
	public InetAddress address;

	@ShowInKishanView
	public int port = NetworkAgent.DEFAULT_PORT;

	public double TokensPerSecond;
	public boolean IsComputing;
	public double promptLag;
	public int queueSize;
	public double alpha = 1.0;

	@ShowInKishanView
	public Connection connection;

	public PeerNode(BGraph g) {
		super(g);
	}

	public void setDirectory(File directory) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
		this.name = directory.getName();

		{
			var publicKeyFile = new File(directory, "public_key.pem");

			if (publicKeyFile.exists()) {
				var publicKeyString = Files.readString(publicKeyFile.toPath());
				byte[] der = Base64.getDecoder().decode(publicKeyString);
				X509EncodedKeySpec spec = new X509EncodedKeySpec(der);
				this.publicKey = KeyFactory.getInstance("RSA").generatePublic(spec);
			} else {
				System.err.println("no public key for " + this);
			}
		}

		{
			var ipFile = new File(directory, "ip.txt");

			if (ipFile.exists()) {
				var ipS = Files.readString(ipFile.toPath()).trim();
				this.address = s2ip(ipS);
			} else {
				System.err.println("no IP known for " + this);
			}
		}
	}

	private static final Pattern IPV4_PATTERN = Pattern
			.compile("^((25[0-5]|(2[0-4]|[0-9])?[0-9])\\.){3}(25[0-5]|(2[0-4]|[0-9])?[0-9])$");

	public static boolean isIPv4(String input) {
		return IPV4_PATTERN.matcher(input).matches();
	}

	public static InetAddress s2ip(String host) throws UnknownHostException {
		if (isIPv4(host)) {
			return InetAddress.getByAddress(ipv4ToBytesManual(host));
		} else {
			return InetAddress.getByName(host);
		}
	}

	public static byte[] ipv4ToBytesManual(String ipStr) {
		String[] parts = ipStr.trim().split("\\.");
		if (parts.length != 4) {
			throw new IllegalArgumentException("Invalid IPv4 format: " + ipStr);
		}

		byte[] bytes = new byte[4];
		for (int i = 0; i < 4; i++) {
			try {
				int val = Integer.parseInt(parts[i]);
				if (val < 0 || val > 255) {
					throw new IllegalArgumentException("Octet out of range [0-255]: " + parts[i]);
				}
				// Cast integer (0..255) to signed Java byte (-128..127)
				bytes[i] = (byte) val;
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Invalid octet number: " + parts[i], e);
			}
		}
		return bytes;
	}

	public double getTokensPerSecond() {
		return TokensPerSecond;
	}

	public double getPromptLagMsPerToken() {
		return promptLag;
	}

	public int getCurrentQueueSize() {
		return queueSize;
	}

	public double getAlpha() {
		return alpha;
	}

	@Override
	public String whatIsThis() {
		return null;
	}

	@Override
	public String toString() {
		if (name != null)
			return name;

		if (address != null)
			return address.getHostName() + ":" + port;

		if (publicKey != null)
			return publicKey.toString();

		return "n/a";
	}

	public double getScore() {
		// calculer Score P2P
		return (TokensPerSecond * alpha) / ((1 + queueSize) * (1 + promptLag));
	}

	public void ensureDisconnected() {
		if (connection != null) {
			connection.close();
			connection = null;
		}
	}
}
