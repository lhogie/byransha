package byransha.network;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import javax.swing.JComponent;

import byransha.graph.ActionMethod;
import byransha.graph.BNode;
import byransha.graph.Root;
import byransha.graph.ShowInKishanView;
import byransha.nodes.system.ChatNode;
import byransha.security.ECC;
import byransha.util.ByUtils;

public class Peer extends BNode {
	List<PeerListener> listeners = new ArrayList<>();

	public List<Peer> neighbors = new ArrayList<>();

	@ShowInKishanView
	public String name;

	@ShowInKishanView
	public PublicKey publicKey;

	public Key sharedSecret; // for NetworkBox.SecretBox

	@ShowInKishanView
	public InetAddress address;

	@ShowInKishanView
	public int port = TCPServer.DEFAULT_PORT;

	@ShowInKishanView
	private Connection connection;

	public PeerInfo lastInfo;

	public boolean autoConnect = true;

	public Peer(Root g) {
		super(g);
	}

	@ShowInKishanView
	public List<String> neighborsName() {
		return lastInfo.neighborsName;
	}

	@ShowInKishanView
	public String os() {
		return lastInfo.systemProperties.getProperty("os.name");
	}

	@ShowInKishanView
	public long uptime() {
		return lastInfo.uptimeMs;
	}

	static List<String> neighborsNames(List<Peer> peers) {
		return peers.stream().map(p -> p.name).toList();
	}

	public void setDirectory(File directory) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
		this.name = directory.getName();

		{
			var publicKeyFile = new File(directory, "public_key.pem");

			if (publicKeyFile.exists()) {
				var publicKeyString = Files.readString(publicKeyFile.toPath());
				
				this.publicKey = ECC.fromPem(publicKeyString, "X25519");
				
				this.autoConnect = !new File(directory, "noAutoConnect").exists();
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

	public static interface PeerListener {
		void connected(Connection c);

		void connectionLost();
	}

	public void setConnection(Connection c) {
		Objects.requireNonNull(c);

		if (connection != null)
			throw new IllegalStateException("already connected");

		this.connection = c;
		listeners.forEach(l -> l.connected(c));
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

	public void ensureDisconnected() {
		if (connection != null) {
			disconnect();
		}
	}

	@ActionMethod
	public void disconnect() {
		if (connection == null)
			throw new IllegalStateException("not connected");

		connection.close();
		connection = null;
		listeners.forEach(l -> l.connectionLost());
	}

	@Override
	protected JComponent getSmallComponent(ChatNode chat) {
		var component = super.getSmallComponent(chat);
		updateColor(component);

		listeners.add(new PeerListener() {

			@Override
			public void connectionLost() {
				updateColor(component);
			}

			@Override
			public void connected(Connection c) {
				updateColor(component);
			}
		});

		return component;
	}

	private void updateColor(JComponent component) {
		component.setBackground(getConnection() == null ? Color.red : Color.green);

	}

	public Connection getConnection() {
		return connection;
	}

	@ActionMethod
	public void tryConnect() {
		System.out.println("trying to connecto " + this);
		ByUtils.thread("opening socket to " + this, () -> {
			try {
				g().networkAgent.tcp.newSocket(new Socket(address, port), true);
			} catch (IOException err) {
				ensureDisconnected();
			}
		});

	}
}
