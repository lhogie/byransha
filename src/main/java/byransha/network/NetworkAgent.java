package byransha.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import butils.RSAEncoder;
import byransha.event.Event;
import byransha.graph.Ack;
import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.nodes.primitive.ListNode;

public class NetworkAgent extends BNode {
	public static final int port = 9876;
	final ListNode<PeerNode> peers;
	final RSAEncoder rsaEncoder;
	DatagramSocket socket;

	public NetworkAgent(BGraph g) {
		super(g);
		this.peers = new ListNode<>(g, "peers");
		rsaEncoder = new RSAEncoder(null);

		new Thread(() -> {
			try {
				socket = new DatagramSocket(port);
				System.out.println("UDP Server is listening on port " + port);

				byte[] receiveBuffer = new byte[1024];

				while (true) {
					DatagramPacket packet = new DatagramPacket(receiveBuffer, receiveBuffer.length);
					socket.receive(packet);
					byte[] encodedData = packet.getData();
					var from = findPeer(packet.getAddress());
					var data = RSAEncoder.decode(from.publicKey, encodedData);
					ByteArrayInputStream bais = new ByteArrayInputStream(data);
					ObjectInputStream ois = new ObjectInputStream(bais);
					handle(ois.readObject(), from);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}, "network agent reception thread").start();

	}

	private void handle(Object received, PeerNode from) {
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

		try {
			var p = new PeerNode(g);
			p.address = address;
			return p;
		} catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException e) {
			error(e);
			return null;
		}
	}

	public synchronized void send(Object o, PeerNode to) throws IOException {
		var bos = new ByteArrayOutputStream();
		var oos = new ObjectOutputStream(bos);
		oos.writeObject(o);
		byte[] data = bos.toByteArray();
		data = rsaEncoder.encode(data);
		var sendPacket = new DatagramPacket(data, data.length, to.address, to.port);
		socket.send(sendPacket);
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
