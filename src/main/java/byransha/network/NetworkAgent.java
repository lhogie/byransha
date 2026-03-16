package byransha.network;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Properties;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

import byransha.event.Event;
import byransha.graph.Ack;
import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.action.list.ListNode;
import byransha.nodes.primitive.StringNode;
import byransha.security.RSA;
import byransha.util.GZip;
import toools.io.ser.JavaSerializer;
import toools.io.ser.Serializer;

public class NetworkAgent extends BNode {
	public static final int port = 9876;
	final StringNode publicKeyInfo;
	final StringNode inOutInfo;
	final ListNode<PeerNode> peers;
	String name;
	DatagramSocket socket;
	private int packetReceived;
	private int packetSent;
	private KeyPair keyPair;

	public NetworkAgent(BGraph g) throws FileNotFoundException, IOException {
		super(g);
		this.peers = new ListNode<>(g, "peers");
		File securityDir = new File(g.byransha.configDirectory, "security");
		File authorizedKeys = new File(securityDir, "authorized_keys");

		if (authorizedKeys.exists()) {
			var props = new Properties();
			props.load(new FileInputStream(authorizedKeys));

			for (var e : props.entrySet()) {
				var name = (String) e.getKey();
				var base64key = (String) e.getValue();
				byte[] der = Base64.getDecoder().decode(base64key);
				X509EncodedKeySpec spec = new X509EncodedKeySpec(der);
				try {
					var pk = KeyFactory.getInstance("RSA").generatePublic(spec);
					var p = new PeerNode(g);
					p.name = name;
					p.publicKey = pk;
					peers.add(p);
				} catch (InvalidKeySpecException | NoSuchAlgorithmException err) {
					error(err);
				}
			}
		} else {
			authorizedKeys.getParentFile().mkdirs();
			Files.write(authorizedKeys.toPath(), "".getBytes());
		}

		publicKeyInfo = new StringNode(g);
		inOutInfo = new StringNode(g);
		File keyPairFile = new File(securityDir, "keyPair.ser");

		if (keyPairFile.exists()) {
			keyPair = (KeyPair) serializer.fromBytes(Files.readAllBytes(keyPairFile.toPath()));
		} else {
			System.out.println("Generating new random RSA keys");
			keyPair = RSA.randomKeyPair();
			keyPairFile.getParentFile().mkdirs();
			Files.write(keyPairFile.toPath(), serializer.toBytes(keyPair));
			var pub = new String(RSA.toBase64(keyPair.getPublic()));
			System.out.println("public key: " + pub);
			publicKeyInfo.set(pub);
		}

		new Thread(() -> {
			try {
				socket = new DatagramSocket(port);
				System.out.println("UDP Server is listening on port " + port);

				byte[] receiveBuffer = new byte[1024];

				while (true) {
					DatagramPacket packet = new DatagramPacket(receiveBuffer, receiveBuffer.length);
					socket.receive(packet);
					var msg = (Message) serializer.fromBytes(GZip.gunzip(packet.getData()));
					++packetReceived;
					updateInOutInfo();
					handle(msg);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}, "network agent reception thread").start();
	}

	public static PrivateKey readPrivateKey(Path path) throws Exception {
		String pem = Files.readString(path);

		String base64 = pem.replaceAll("-----BEGIN [A-Z ]+-----", "").replaceAll("-----END [A-Z ]+-----", "")
				.replaceAll("\\s", "");

		byte[] der = Base64.getDecoder().decode(base64);

		// Try PKCS#8 first (BEGIN PRIVATE KEY)
		try {
			return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(der));
		} catch (InvalidKeySpecException e) {
			// Fall back to PKCS#1 (BEGIN RSA PRIVATE KEY) via BouncyCastle
			return readPkcs1PrivateKey(path);
		}
	}

	// BouncyCastle fallback for PKCS#1 format
	private static PrivateKey readPkcs1PrivateKey(Path path) throws Exception {
		try (PEMParser parser = new PEMParser(Files.newBufferedReader(path))) {
			Object obj = parser.readObject();
			if (obj instanceof PEMKeyPair pemKeyPair) {
				return new JcaPEMKeyConverter().setProvider("BC").getKeyPair(pemKeyPair).getPrivate();
			}
			if (obj instanceof PrivateKeyInfo info) {
				return new JcaPEMKeyConverter().setProvider("BC").getPrivateKey(info);
			}
			throw new IllegalArgumentException("Unrecognized key format: " + obj.getClass());
		}
	}

	private void handle(Message msg) throws IOException, ClassNotFoundException {
		var from = findPeer(msg.from.getLast());

		if (from.publicKey != null) {
			msg.data = RSA.decrypt(msg.data, keyPair.getPrivate());
		}

		var received = serializer.fromBytes(msg.data);

		if (received instanceof Ack ack) {
			g.eventList.findEvent(ack.id).markReceivedBy(from);
		} else if (received instanceof Event e) {
			var alreadyKnownEvent = g.eventList.findEvent(e.ID);

			if (alreadyKnownEvent != null) {
				alreadyKnownEvent.commitToDisk();
				alreadyKnownEvent.markReceivedBy(from);
			} else {
				try {
					g.eventList.add(e);
					e.markReceivedBy(from);
				} catch (IOException e1) {
					error(e1, true);
				}
			}

			try {
				send(new Ack(e.ID));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} else {
			throw new IllegalStateException("received " + received.getClass());
		}
	}

	private PeerNode findPeer(InetAddress address) {
		for (var p : peers.get()) {
			if (p.address.equals(address)) {
				return p;
			}
		}

		return null;
	}

	private PeerNode findPeer(String name) {
		for (var p : peers.get()) {
			if (p.name.equals(name)) {
				return p;
			}
		}

		return null;
	}

	private final Serializer serializer = new JavaSerializer<>();

	public synchronized void send(Object o, PeerNode to) throws IOException {
		var msg = new Message();
		msg.from.add(name);
		msg.data = serializer.toBytes(o);
		msg.data = RSA.encrypt(msg.data, to.publicKey);
		var msgBytes = GZip.gzip(serializer.toBytes(msg));
		var sendPacket = new DatagramPacket(msgBytes, msgBytes.length, to.address, to.port);
		socket.send(sendPacket);
		++packetSent;
		updateInOutInfo();
	}

	private void updateInOutInfo() {
		inOutInfo.set(packetReceived + " received, " + packetSent + " sent");
	}

	public synchronized void send(Object o) throws IOException {
		for (var to : peers.get()) {
			send(o, to);
		}
	}

	@Override
	public String whatIsThis() {
		return "network agent";
	}

	@Override
	public String prettyName() {
		return "network agent";
	}
}
