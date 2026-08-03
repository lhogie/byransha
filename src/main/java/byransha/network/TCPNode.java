package byransha.network;

import java.io.IOException;
import java.net.Socket;
import java.util.Objects;

import byransha.graph.ServiceNode;
import byransha.graph.ShowInKishanView;
import byransha.security.NetworkBox;
import byransha.util.ByUtils;

public class TCPNode extends ServiceNode {

	@ShowInKishanView
	private TCPClient client;

	@ShowInKishanView
	private TCPServer server;

	public TCPNode(NetworkAgent net, int port) {
		super(net);
		this.client = new TCPClient(this);
		this.server = new TCPServer(this, port);
	}

	void newSocket(Socket sock, boolean sendNameFirst) {
//		System.out.println("new socket from " + sock.getInetAddress());
		try {
			Connection connection = new Connection(sock);
			String other = handshake(sendNameFirst, connection);
			var peer = g().networkAgent.neighborhood.findPeerByName(other);

			if (peer == null) {
				System.out.println("rejecting unknown peer " + other + " at " + sock.getInetAddress());
				connection.close();
			} else if (peer.getConnection() != null) {
				System.out.println("already connected to peer " + other);
			} else {
				peer.setConnection(connection);
				peer.sharedSecret = NetworkBox.agreeOnSharedSecret(g().networkAgent.privateKey, peer.publicKey);
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
		var name = g().networkAgent.name.get();
		Objects.requireNonNull(name);
		var msg = new Message();
		msg.content = ByUtils.serializer.toBytes(name);

		if (sendNameFirst) {
			to.write(msg);
			return (String) ByUtils.serializer.fromBytes(to.readMessage().content);
		} else {
			var other = (String) ByUtils.serializer.fromBytes(to.readMessage().content);
			to.write(msg);
			return other;
		}
	}

	private void tcpSocketReadingThread(Peer p) {
		ByUtils.thread("thread waiting for messages from", () -> {
			try {
				while (true) {
					var wireMsg = p.getConnection().readMessage();
					
					// Decrypt the Hop-by-Hop bytes
					byte[] hopDecrypted = NetworkBox.decryptFast(p.sharedSecret, wireMsg.content);
					
					Message msg = (Message) ByUtils.serializer.fromBytes(hopDecrypted);

					msg.routingInfo.actualRoute.add(p.name);
					onNewMessage(msg);
				}
			} catch (Exception err) {
//				err.printStackTrace();
				p.ensureDisconnected();
				System.out.println(p + " left");
			}
		});
	}

	public void start() {
		server.start();
		client.start();
	}

}
