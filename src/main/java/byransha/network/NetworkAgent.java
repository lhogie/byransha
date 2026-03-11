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
import java.util.ArrayList;
import java.util.List;

import butils.RSAEncoder;
import byransha.event.Event;
import byransha.graph.Ack;
import byransha.graph.BGraph;
import byransha.graph.BNode;

public class NetworkAgent extends BNode {
	List<PeerNode> peers = new ArrayList<>();
	final RSAEncoder rsaEncoder;
	DatagramSocket socket;

	public NetworkAgent(BGraph g) {
		super(g);
		rsaEncoder = new RSAEncoder(null);
		int port = 9876;

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
		}, "network agent thread").start();
	}

	public static class Events extends ArrayList<Event> {

	}

	private void handle(Object received, PeerNode from) {
		if (received instanceof Ack ack) {
			g.eventList.findEvent(ack.eventID).markReceivedBy(from);
		} else if (received instanceof Events events) {
			for (var e : events) {
				var alreadyKnownEvent = g.eventList.findEvent(e.ID);

				if (alreadyKnownEvent != null) {
					alreadyKnownEvent.markReceivedBy(from);
					alreadyKnownEvent.commitToDisk();
				} else {
					e.markReceivedBy(from);
					g.eventList.add(e);
				}
			}
		}
	}

	private PeerNode findPeer(InetAddress address) {
		for (var p : peers) {
			if (p.address.equals(address)) {
				return p;
			}
		}

		try {
			PeerNode p;
			p = new PeerNode(g);
			p.address = address;
			return p;
		} catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException e) {
			error(e);
			return null;
		}
	}

	private PeerNode findPeer(int hash) {
		for (var p : peers) {
			if (p.peerID() == hash) {
				return p;
			}
		}

		return null;
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
		for (var to : peers) {
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
