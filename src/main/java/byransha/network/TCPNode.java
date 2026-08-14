package byransha.network;

import java.io.IOException;
import java.net.Socket;

import byransha.Service;
import byransha.action.base.ShowInKishanView;
import byransha.security.NetworkBox;
import byransha.thread.ThreadNode;
import byransha.util.ByUtils;

public class TCPNode extends Service {

	@ShowInKishanView
	private TCPServer server;

	public TCPNode(Network net, int port) {
		super(net);
		this.server = new TCPServer(this, port);
	}

	void newSocket(Socket sock, boolean sendNameFirst) {
//		System.out.println("new socket from " + sock.getInetAddress());
		try {
			Connection connection = new Connection(sock);
			String nameOfPeerAtOtherHand = handshake(sendNameFirst, connection);
			var other = hub().network.neighborhood.findPeerByName(nameOfPeerAtOtherHand);

			if (other == null) {
				other = new OtherPeer(hub().network.neighborhood, nameOfPeerAtOtherHand);
				System.out.println("rejecting untrusted peer " + other.name + " at " + sock.getInetAddress());
				connection.close();
			} else if (other.getConnection() != null) {
				System.out.println("already connected to peer " + nameOfPeerAtOtherHand);
			} else {
				other.setConnection(connection);

				if (other.publicKey != null) {
					other.sharedSecret = NetworkBox.agreeOnSharedSecret(hub().network.neighborhood.self.privateKey,
							other.publicKey);
					System.out.println(other + " joined");
					tcpSocketReadingThread(other);
				} else {
					System.err.println("Warning: No public key for " + other.name + ". rejecting peer");
					other.ensureDisconnected();
				}
			}
		} catch (IOException err) {
			System.out.println("gone at handshake");
		} catch (ClassNotFoundException err) {
			throw new IllegalStateException();
		}
	}

	private String handshake(boolean sendNameFirst, Connection to) throws IOException, ClassNotFoundException {
		String name = hub().network.neighborhood.self.name;

		if (sendNameFirst) {
			to.writeObject(name);
			return (String) to.readObject();
		} else {
			var other = (String) to.readObject();
			to.writeObject(name);
			return other;
		}
	}

	private void tcpSocketReadingThread(Peer peer) {
		new ThreadNode(this, "thread waiting for messages from", () -> {
			try {
				while (true) {
					byte[] wireMsg = (byte[]) peer.getConnection().readObject();
					((Network) parent).processIncomingMessage(wireMsg, peer);
				}
			} catch (Exception err) {
				err.printStackTrace();
				peer.ensureDisconnected();
				System.out.println(peer + " left");
			}
		});
	}

	public void start() {
		server.start();
	}

	@Override
	protected void incomingMessage(Message msg) {
		// TODO Auto-generated method stub

	}

}
