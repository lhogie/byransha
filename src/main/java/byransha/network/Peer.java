package byransha.network;

import java.awt.Color;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.Key;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import javax.swing.JComponent;

import byransha.graph.ActionMethod;
import byransha.graph.AddButtonOnKishanView;
import byransha.graph.BNode;
import byransha.graph.LoopingThreadNode;
import byransha.graph.ShowInKishanView;
import byransha.graph.ThreadNode;
import byransha.network.PeerManager.Gossip;
import byransha.primitive.BooleanNode;
import byransha.primitive.DoubleNode;
import byransha.system.ChatNode;

public abstract class Peer extends BNode {

	public List<Peer> neighbors = new ArrayList<>();

	@ShowInKishanView
	public final String name;

	@ShowInKishanView
	public PublicKey publicKey;

	public Key sharedSecret; // for NetworkBox.SecretBox

	@ShowInKishanView
	public InetAddress address;

	@ShowInKishanView
	public int port = TCPServer.DEFAULT_PORT;

	@ShowInKishanView
	private Connection connection;

	public Gossip lastGossip;

	@ShowInKishanView
	public BooleanNode autoConnect = new BooleanNode(this, true);

	@ShowInKishanView
	final DoubleNode periodS = new DoubleNode(this, 5);

	public Boolean haveAi;

	public Peer(PeerManager neigh, String name) {
		super(neigh);
		Objects.requireNonNull(name);
		this.name = name;

		new LoopingThreadNode(this, () -> periodS.get(), "auto connecto to " + this, () -> {
			if (autoConnect.get()) {
				if (getConnection() == null && address != null) {
					try {
						hub().network.tcp.newSocket(new Socket(address, port), true);
					} catch (IOException e) {
						ensureDisconnected();
					}
				} else {
//					System.out.println("already connected to " + this + " or no address");
				}
			}
		});
	}

	@ShowInKishanView
	public List<String> neighborsName() {
		return lastGossip != null ? lastGossip.neighborsName() : Collections.emptyList();
	}

	@ShowInKishanView
	public int distance() {
		return route().size();
	}

	@ShowInKishanView
	public List<Peer> route() {
		return hub().network.sender.routingProtocol.computeRouteToReach(this);
	}

	@ShowInKishanView
	public List<String> routeNames() {
		var route = route();
		return route == null ? null : route.stream().map(p -> p.name).toList();
	}

	static List<String> neighborsNames(List<Peer> peers) {
		return peers.stream().map(p -> p.name).toList();
	}

	public void setConnection(Connection c) {
		Objects.requireNonNull(c);

		if (connection != null)
			throw new IllegalStateException("already connected");

		this.connection = c;
		hub().network.neighborhood.neighborhoodListeners.forEach(l -> l.joined(this));
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
		return "a participant to the network";
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
		hub().network.neighborhood.neighborhoodListeners.forEach(l -> l.left(this));
	}

	@Override
	protected JComponent getSmallComponent(ChatNode chat) {
		var component = super.getSmallComponent(chat);
		updateColor(component);

		hub().network.neighborhood.neighborhoodListeners.add(new NeighborhoodListener() {

			@Override
			public void left(Peer p) {
				updateColor(component);
			}

			@Override
			public void joined(Peer p) {
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
	@AddButtonOnKishanView
	public void tryConnect() {
		System.out.println("trying to connecto " + this);
		new ThreadNode(this, "opening socket to " + this, () -> {
			try {
				hub().network.tcp.newSocket(new Socket(address, port), true);
			} catch (IOException err) {
				ensureDisconnected();
			}
		});
	}
}
