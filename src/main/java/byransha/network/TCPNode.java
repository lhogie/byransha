package byransha.network;

import java.io.IOException;
import java.net.Socket;

import byransha.graph.ServiceNode;
import byransha.graph.ShowInKishanView;
import byransha.security.NetworkBox;
import byransha.util.ByUtils;

public class TCPNode extends ServiceNode {

	@ShowInKishanView
	private TCPServer server;

	public TCPNode(NetworkAgent net, int port) {
		super(net);
		this.server = new TCPServer(this, port);
	}

	void newSocket(Socket sock, boolean sendNameFirst) {
//		System.out.println("new socket from " + sock.getInetAddress());
		try {
			Connection connection = new Connection(sock);
			String other = handshake(sendNameFirst, connection);
			var peer = hub().networkAgent.neighborhood.findPeerByName(other);

			if (peer == null) {
				System.out.println("rejecting unknown peer " + other + " at " + sock.getInetAddress());
				connection.close();
			} else if (peer.getConnection() != null) {
				System.out.println("already connected to peer " + other);
			} else {
				peer.setConnection(connection);
				if (peer.publicKey != null) {
					peer.sharedSecret = NetworkBox.agreeOnSharedSecret(hub().networkAgent.neighborhood.self.privateKey, peer.publicKey);
				} else {
					System.out.println("Warning: No public key for " + peer.name
							+ ". Secure routing disabled until key is added.");
				}
				System.out.println(peer + " joined");
				tcpSocketReadingThread(peer);
			}
		} catch (IOException err) {
			System.out.println("gone at handshake");
		} catch (ClassNotFoundException err) {
			hub().errorLog.add(err);
		}
	}

	private String handshake(boolean sendNameFirst, Connection to) throws IOException, ClassNotFoundException {
		String name = hub().networkAgent.neighborhood.self.name;

		if (sendNameFirst) {
			to.writeObject(name);
			return (String) to.readObject();
		} else {
			var other = (String) to.readObject();
			to.writeObject(name);
			return other;
		}
	}

	private void tcpSocketReadingThread(Peer p) {
		ByUtils.thread("thread waiting for messages from", () -> {
			try {
				while (true) {
					byte[] wireMsg = (byte[]) p.getConnection().readObject();

					if (p.sharedSecret == null) {
						System.out.println("Ignoring packet from " + p.name + ": missing public key/shared secret.");
						continue;
					}

					byte[] hopDecrypted = NetworkBox.decryptFast(p.sharedSecret, wireMsg);
					Message msg = (Message) ByUtils.serializer.fromBytes(hopDecrypted);

					msg.routingInfo.actualRoute.add(p.name);
					((NetworkAgent) parent).processIncomingMessage(msg);
				}
			} catch (Exception err) {
				err.printStackTrace();
				p.ensureDisconnected();
				System.out.println(p + " left");
			}
		});
	}

	public void start() {
		server.start();
	}

}
