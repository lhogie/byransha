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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import byransha.event.Event;
import byransha.graph.Ack;
import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.ServiceNode;
import byransha.graph.ShowInKishanView;
import byransha.graph.list.action.ListNode;
import byransha.nodes.primitive.BooleanNode;
import byransha.nodes.primitive.DoubleNode;
import byransha.nodes.primitive.StringNode;
import byransha.nodes.system.Byransha;
import byransha.security.RSA;
import byransha.util.ByUtils;
import byransha.util.Q;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import toools.io.ser.JavaSerializer;
import toools.io.ser.Serializer;

public class NetworkAgent extends ServiceNode {
	public static final int DEFAULT_PORT = 50170;
	ServerSocket socket;
	@ShowInKishanView
	public final int port;
	protected int packetReceived;
	protected int messageSent;
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
	final BooleanNode bcastNeighbors = new BooleanNode(this, true);

	@ShowInKishanView
	final DoubleNode bcastNeighboorhoodPeriodS = new DoubleNode(this, 1);

	@ShowInKishanView
	final DoubleNode tryConnectPeriodS = new DoubleNode(this, 1);
	@ShowInKishanView
	final StringNode inOutInfo = new StringNode(this);
	@ShowInKishanView
	public final ListNode<PeerNode> peers = new ListNode<>(this, "peers", PeerNode.class);
	@ShowInKishanView
	public PublicKey publicKey;
	public PrivateKey privateKey;
	public static final Serializer serializer = new JavaSerializer<>();

	private Q<Message> sendingBox = new Q<>(1000);

	public NetworkAgent(BGraph g, int port)
			throws FileNotFoundException, IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		super(g);
		this.port = port;

		loadPublicAndPrivateKeys();
		loadPeersFromDisk();

		ByUtils.thread("TCP server thread", () -> {
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
		});

		ByUtils.thread("TCP client thread", () -> {
			while (true) {
				if (periodicTryConnect.get()) {
					for (var p : peers.elements) {
						if (p.getConnection() == null && p.address != null) {
							ByUtils.thread("opening socket to " + p, () -> {
								try {
									System.out.println("trying to connecto " + p);
									newSocket(new Socket(p.address, p.port), true);
								} catch (IOException err) {
									p.ensureDisconnected();
								}
							});
						}
					}
				}

				sleep(tryConnectPeriodS.get());
			}
		});

		ByUtils.thread("message sending", () -> {
			while (true) {
				var msg = sendingBox.poll_sync();
				var recipient = findPeerByName(msg.routingInfo.recipient());
				var route = computeRouteToReach(recipient);
				msg.routingInfo.suggestedRoute = route.stream().map(p -> p.name).toList();
				var relay = route.getFirst();

				if (relay.getConnection() != null) {
					try {
						relay.getConnection().write(msg);
						++messageSent;
						updateInOutInfo();
					} catch (IOException e) {
						sendingBox.add_sync(msg);
					}
				} else {
					sendingBox.add_sync(msg);
				}
			}
		});

		ByUtils.thread("forward neighborhood", () -> {
			while (true) {
				if (bcastNeighbors.get()) {
					var neighbors = neighbors();
					var names = PeerNode.neighborsNames(neighbors);

					for (var p : neighbors) {
						try {
							sendObject(names, p, null);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}

				sleep(bcastNeighboorhoodPeriodS.get());
			}
		});
	}

	public List<PeerNode> neighbors() {
		return peers.elements.stream().filter(p -> p.getConnection() != null).toList();
	}

	@ShowInKishanView
	public int nbMessageInSendingQueue() {
		return sendingBox.size();
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

		ByUtils.thread("discover peers info on disk", () -> {
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
		});

	}

	private void newSocket(Socket sock, boolean sendNameFirst) {
//		System.out.println("new socket from " + sock.getInetAddress());
		try {
			Connection connection = new Connection(sock);
			String other = handshake(sendNameFirst, connection);
			var peer = findPeerByName(other);

			if (peer == null) {
				System.out.println("rejecting unknown peer " + other + " at " + sock.getInetAddress());
				connection.close();
			} else if (peer.getConnection() != null) {
				System.out.println("already connected to peer " + other);
			} else {
				peer.setConnection(connection);
				System.out.println(peer + " joined");
				tcpSocketReadingThread(peer);
			}
		} catch (IOException err) {
			System.out.println("gone at handshake");
		} catch (ClassNotFoundException err) {
			g().errorLog.add(err);
		}
	}

	private String handshake(boolean sendNameFirst, Connection to) throws IOException, ClassNotFoundException {
		var name = this.name.get();
		Objects.requireNonNull(name);
		var msg = new Message();
		msg.content = serializer.toBytes(name);

		if (sendNameFirst) {
			to.write(msg);
			return (String) serializer.fromBytes(to.readMessage().content);
		} else {
			var other = (String) serializer.fromBytes(to.readMessage().content);
			to.write(msg);
			return other;
		}
	}

	private void tcpSocketReadingThread(PeerNode p) {
		ByUtils.thread("thread waiting for messages from", () -> {
			try {
				while (true) {
					var msg = p.getConnection().readMessage();
					msg.routingInfo.actualRoute.add(p.name);
					onNewMessage(msg);
				}
			} catch (Exception err) {
				err.printStackTrace();
				p.ensureDisconnected();
				System.out.println(p + " left");
			}
		});
	}

	@Override
	public synchronized void onNewMessage(Message msg) {
		System.out.println("*** message received: " + msg);
		++nbMessagesReceived;
		updateInOutInfo();

		var from = findPeerByName(msg.routingInfo.source());
		boolean imTheRecipient = msg.routingInfo.recipient().equals(name);

		if (imTheRecipient) {
			var content = serializer.fromBytes(msg.content);

			if (content instanceof Ack ack) {
				g().eventList.findEvent(ack.id).markReceivedBy(from);
			} else if (content instanceof Event e) {
				var alreadyKnownEvent = g().eventList.findEvent(e.id());

				if (alreadyKnownEvent != null) {
					alreadyKnownEvent.markReceivedBy(from);
				} else {
					g().eventList.add(e);
					e.markReceivedBy(from);
				}
			} else if (content instanceof NeighborList e) {
				var p = findPeerByName(e.src);
				p.neighbors = e.stream().map(name -> {
					var peer = findPeerByName(name);

					if (peer == null) {
						peer = new PeerNode(g());
						peer.name = name;
						peers.elements.add(peer);
					}
					return peer;
				}).toList();
				considerForwarding(msg, null);
			} else if (content instanceof PeerTelemetry t) {
				if (from != null) {
					from.TokensPerSecond = t.tokensPerSecond;
					from.IsComputing = t.isComputing;
					from.promptLag = t.promptLag;
					from.queueSize = t.queueSize;
					if (t.alpha > 0)
						from.alpha = t.alpha;
				}
			} else if (msg instanceof ServiceLevelMessage slm) {
				for (var service : g().indexes.byClass.getClassNodeFor(slm.recipient).allInstances().elements) {
					((ServiceNode) service).onNewMessage(msg);
				}
			} else {

			}
		} else {
			considerForwarding(msg, null);
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

	public void sendObject(Object o, PeerNode to, Consumer<Message> c) throws IOException {
		var msg = new Message();
		msg.routingInfo.suggestedRoute.add(to.name);
		msg.routingInfo.actualRoute.add(name.get());
		msg.content = NetworkAgent.serializer.toBytes(o);

		if (c != null) {
			c.accept(msg);
		}

		sendingBox.add_sync(msg);
	}

	public void bcast(Object o, Consumer<Message> c) throws IOException {
		for (var p : neighbors()) {
			sendObject(o, p, c);
		}
	}

	public void considerForwarding(Message msg, Consumer<Message> c) {
		if (msg.routingInfo.actualRoute.contains(name))
			return;

		sendingBox.add_sync(msg);
	}

	private List<PeerNode> computeRouteToReach(PeerNode p) {
		var preds = bfs();
		List<PeerNode> r = new ArrayList<PeerNode>();

		while (p != null) {
			var pred = preds.get(p);
			r.add(pred);
			p = pred;
		}

		Collections.reverse(r);
		return r;
	}

	public Object2ObjectOpenHashMap<PeerNode, PeerNode> bfs() {
		List<PeerNode> q = new ArrayList<>();
		var preds = new Object2ObjectOpenHashMap<PeerNode, PeerNode>();
		Set<BNode> visited = new HashSet<>();

		for (PeerNode p : neighbors()) {
			q.add(p);
		}

		while (!q.isEmpty()) {
			PeerNode p = q.removeFirst();

			for (PeerNode succ : p.neighbors) {
				if (!visited.contains(succ)) {
					visited.add(succ);
					q.add(succ);
					preds.put(succ, p);
				}
			}
		}

		return preds;
	}

	private void updateInOutInfo() {
		inOutInfo.set(nbMessagesReceived + " received, " + messageSent + " sent");
	}

	@Override
	public String whatIsThis() {
		return "network agent";
	}

	@Override
	public String toString() {
		return "received: " + nbMessagesReceived + ", sent: " + messageSent;
	}
}
