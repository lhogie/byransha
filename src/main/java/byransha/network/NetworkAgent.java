package byransha.network;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Objects;

import byransha.event.Event;
import byransha.graph.Ack;
import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.ShowInKishanView;
import byransha.graph.list.action.ListNode;
import byransha.nodes.primitive.BooleanNode;
import byransha.nodes.primitive.DoubleNode;
import byransha.nodes.primitive.StringNode;
import byransha.nodes.system.Byransha;
import byransha.security.RSA;
import toools.io.ser.JavaSerializer;
import toools.io.ser.Serializer;

public class NetworkAgent extends BNode {
	public static final int DEFAULT_PORT = 50170;
	ServerSocket socket;
	@ShowInKishanView
	public final int port;
	protected int packetReceived;
	protected int packetSent;
	private int nbMessagesReceived;

	@ShowInKishanView
	public static final File peersDirectory = new File(Byransha.homeDirectory, "peers");

	@ShowInKishanView
	StringNode name = new StringNode(this, System.getProperty("user.name"), "([a-z][A-Z])+");

	@ShowInKishanView
	File securityDir = new File(Byransha.homeDirectory, "security");
	@ShowInKishanView
	File authorizedKeys = new File(securityDir, "authorized_keys");

	@ShowInKishanView
	final StringNode publicKeyInfo = new StringNode(this);

	@ShowInKishanView
	final BooleanNode periodicTryConnect = new BooleanNode(this, true);

	@ShowInKishanView
	final DoubleNode tryConnectPeriod = new DoubleNode(this, 1);
	@ShowInKishanView
	final StringNode inOutInfo = new StringNode(this);
	@ShowInKishanView
	public final ListNode<PeerNode> peers = new ListNode<>(this, "peers", PeerNode.class);
	@ShowInKishanView
	public PublicKey publicKey;
	public PrivateKey privateKey;
	public static final Serializer serializer = new JavaSerializer<>();

	public NetworkAgent(BGraph g, int port)
			throws FileNotFoundException, IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		super(g);
		this.port = port;

		loadPublicAndPrivateKeys();
		loadPeersFromDisk();

		new Thread(() -> {
			while (true) {
				try {
					socket = new ServerSocket(port);
					System.out.println("TCP Server is listening on port " + port);

					while (true) {
						newSocket(socket.accept(), false);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

				sleep(1);
			}
		}, "TCP server thread").start();

		new Thread(() -> {
			while (true) {
				if (periodicTryConnect.get()) {
					System.out.println("trying to connecto " + peers.elements);
					for (var p : peers.elements) {
						if (p.getConnection() == null && p.address != null) {
							new Thread(() -> {
								try {
									newSocket(new Socket(p.address, p.port), true);
								} catch (IOException err) {
									p.ensureDisconnected();
								}
							}).start();
						}
					}
				}

				sleep(tryConnectPeriod.get());
			}
		}, "TCP client thread").start();
	}

	private void loadPublicAndPrivateKeys() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
		{
			File publicKeyFile = new File(securityDir, "public_key.pem");
			File privateKeyFile = new File(securityDir, "private_key.pem");

			if (publicKeyFile.exists() && privateKeyFile.exists()) {
				this.publicKey = (PublicKey) RSA.fromPem(Files.readString(publicKeyFile.toPath()));
				this.privateKey = (PrivateKey) RSA.fromPem(Files.readString(privateKeyFile.toPath()));
			} else {
				System.out.println("Generating new random RSA keys");
				var keyPair = RSA.randomKeyPair();
				this.publicKey = keyPair.getPublic();
				this.privateKey = keyPair.getPrivate();
				publicKeyFile.getParentFile().mkdirs();
				Files.writeString(publicKeyFile.toPath(), RSA.toPem(publicKey));
				Files.writeString(privateKeyFile.toPath(), RSA.toPem(privateKey));
				var pub = new String(RSA.toBase64(keyPair.getPublic()));
				System.out.println("public key: " + pub);
				publicKeyInfo.set(pub);
			}
		}
	}

	private void loadPeersFromDisk() {
		peersDirectory.mkdirs();

		for (File f : peersDirectory.listFiles()) {
			if (f.isDirectory()) {
				try {
					var peer = new PeerNode(g());
					peer.setDirectory(f);
					peers.elements.add(peer);
					System.out.println("adding " + peer);
				} catch (InvalidKeySpecException | NoSuchAlgorithmException | IOException e) {
					e.printStackTrace();
				}
			}
		}

		new Thread(() -> {
			while (true) {
				for (File peerDirectory : peersDirectory.listFiles()) {
					if (peerDirectory.isDirectory()) {
						var peer = findPeerByName(peerDirectory.getName());

						if (peer == null) {
							try {
								peer = new PeerNode(g());
								peer.setDirectory(peerDirectory);
								peers.elements.add(peer);
							} catch (InvalidKeySpecException | NoSuchAlgorithmException | IOException e) {
								e.printStackTrace();
							}
						}
					}
				}

				sleep(1.2);
			}
		}, "discover peers info on disk").start();

	}

	private void newSocket(Socket sock, boolean sendNameFirst) {
//		System.out.println("new socket from " + sock.getInetAddress());
		try {
			Connection connection = new Connection(sock);
			String other = handshake(sendNameFirst, connection);
			var peer = findPeerByName(other);

			if (peer == null) {
				System.out.println("rejected peer " + other + " from " + sock.getInetAddress());
				// unknown peer
				connection.close();
			} else if (peer.getConnection() != null) {
				System.out.println("already connected to peer " + other);
				// already connected to that peer
			} else {
				peer.setConnection(connection);
				System.out.println("new connection to " + peer);
				tcpSocketReadingThread(peer);
			}
		} catch (IOException err) {
			System.out.println("gone before handshake");
		} catch (ClassNotFoundException err) {
			g().errorLog.add(err);
		}
	}

	private String handshake(boolean sendNameFirst, Connection connection) throws IOException, ClassNotFoundException {
		var name = this.name.get();
		Objects.requireNonNull(name);

		if (sendNameFirst) {
			sendObject(name, connection);
			return (String) connection.readMessage().content;
		} else {
			var other = (String) connection.readMessage().content;
			sendObject(name, connection);
			return other;
		}
	}

	private void tcpSocketReadingThread(PeerNode p) {
		new Thread(() -> {
			try {
				while (true) {
					onNewMessage(p.getConnection().readMessage());
				}
			} catch (IOException err) {
				p.ensureDisconnected();
				System.out.println(p + " left");
			} catch (ClassNotFoundException err) {
				g().errorLog.add(err);
			}
		}, "thread waiting for messages from").start();
	}

	@Override
	protected synchronized void onNewMessage(Message msg) {
		System.out.println("*** message received: " + msg);
		++nbMessagesReceived;
		updateInOutInfo();

		var from = findPeerByName(msg.route.getLast());

		if (msg.content instanceof Ack ack) {
			g().eventList.findEvent(ack.id).markReceivedBy(from);
		} else if (msg.content instanceof Event e) {
			var alreadyKnownEvent = g().eventList.findEvent(e.id());

			if (alreadyKnownEvent != null) {
				alreadyKnownEvent.markReceivedBy(from);
			} else {
				g().eventList.add(e);
				e.markReceivedBy(from);
			}

			try {
				sendObject(new Ack(e.id()));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} else if (msg.content instanceof PeerTelemetry t) {
			if (from != null) {
				from.TokensPerSecond = t.tokensPerSecond;
				from.IsComputing = t.isComputing;
				from.promptLag = t.promptLag;
				from.queueSize = t.queueSize;
				if (t.alpha > 0)
					from.alpha = t.alpha;
			}
		} else {
			throw new IllegalStateException("received " + msg.content.getClass());
		}
	}

	private PeerNode findPeerByName(String name) {
		for (var p : peers.get()) {
			if (p.name != null && p.name.equals(name)) {
				return p;
			}
		}

		return null;
	}

	public PeerNode findPeer(int id) {
		for (var p : g().networkAgent.peers.get()) {
			if (p.id == id) {
				return p;
			}
		}

		return null;
	}

	public synchronized void sendObject(Object o, Connection to) throws IOException {
		var msg = new Message();
		msg.route.add(name.get());
		msg.content = o;
		to.write(msg);
		++packetSent;
		updateInOutInfo();
	}

	private void updateInOutInfo() {
		inOutInfo.set(nbMessagesReceived + " received, " + packetSent + " sent");
	}

	public synchronized void sendObject(Object o) throws IOException {
		for (var to : peers.get()) {
			if (to.getConnection() != null) {
				sendObject(o, to.getConnection());
			}
		}
	}

	@Override
	public String whatIsThis() {
		return "network agent";
	}

	@Override
	public String toString() {
		return "received: " + nbMessagesReceived + ", sent: " + packetSent;
	}
}
