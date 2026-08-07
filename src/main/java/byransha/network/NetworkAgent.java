package byransha.network;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import byransha.graph.Hub;
import byransha.graph.ShowInKishanView;
import byransha.primitive.StringNode;
import byransha.security.NetworkBox;
import byransha.security.PublicKeyImporter;
import byransha.system.SystemNode;
import byransha.util.ByUtils;

public class NetworkAgent extends SystemNode {
	protected int nbMsgReceived;

	@ShowInKishanView
	final StringNode receptionInfo = new StringNode(this);

	@ShowInKishanView
	public final Sender sender;

	@ShowInKishanView
	public final PeerManager neighborhood;

	@ShowInKishanView
	public final TCPNode tcp;

	@ShowInKishanView
	public final PublicKeyImporter publicKeyImporter = new PublicKeyImporter(this);

	public NetworkAgent(Hub g, int port)
			throws FileNotFoundException, IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		super(g);
		this.neighborhood = new PeerManager(this);
		this.sender = new Sender(this);
		this.tcp = new TCPNode(this, port);
	}

	public void start() throws FileNotFoundException, IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		this.neighborhood.start();
		this.sender.start();
		this.tcp.start();
	}

	@Override
	public String whatIsThis() {
		return "network agent";
	}

	@Override
	public String toString() {
		return "received: " + nbMsgReceived;
	}

	private void updateInOutInfo() {
		receptionInfo.set(nbMsgReceived + " received");
	}

	public synchronized void processIncomingMessage(Message msg) {
		++nbMsgReceived;
		updateInOutInfo();

		String nameOfSender = msg.routingInfo.nameOfSender();

		boolean imTheRecipient = msg.routingInfo.nameOfRecipient().equals(neighborhood.self.name);

		if (imTheRecipient) {
			var sender = neighborhood.findPeerByName(nameOfSender);
			System.out.println(
					"*** message received from " + nameOfSender + " (sender: " + msg.routingInfo.actualRoute + ")");

			byte[] decryptedE2E = NetworkBox.decrypt(neighborhood.self.privateKey, sender.publicKey, msg.content);
//			msg.content = decryptedE2E;
			msg.ooInfos.content = ByUtils.serializer.fromBytes(decryptedE2E);

			System.out.println("*** message received: " + msg);
			System.out.println("*** content: " + msg.ooInfos.content);

			var recipientQ = (MessageQ) hub().indexes.byId.get(msg.recipientNode);

			if (recipientQ != null) {
				recipientQ.q.add_sync(msg);
			} else {
				System.err.println("Warning: No recipient node found for message " + msg);
			}
		} else {
			sender.considerForwarding(msg, null);
		}
	}
}
