package byransha.network;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

import byransha.graph.PublicKeyImporter;
import byransha.graph.Root;
import byransha.graph.ServiceNode;
import byransha.graph.ShowInKishanView;
import byransha.nodes.primitive.StringNode;
import byransha.security.NetworkBox;
import byransha.util.ByUtils;

public class NetworkAgent extends ServiceNode {
	protected int nbMsgReceived;

	public PrivateKey privateKey;
	public PublicKey publicKey;

	@ShowInKishanView
	final StringNode receptionInfo = new StringNode(this);

	@ShowInKishanView
	StringNode name = new StringNode(this, System.getProperty("user.name"), "([a-z][A-Z])+");

	@ShowInKishanView
	public final MessageSendQueue sendQ;

	@ShowInKishanView
	public final Neighborhood neighborhood;

	@ShowInKishanView
	public final Gossiper gossiper;

	@ShowInKishanView
	public final TCPNode tcp;

	@ShowInKishanView
	public final PublicKeyImporter publicKeyImporter = new PublicKeyImporter(this);

	public NetworkAgent(Root g, int port)
			throws FileNotFoundException, IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		super(g);

		KeyPair kp = byransha.security.LocalIdentity.loadOrGenerateRoutingKeys();
		this.privateKey = kp.getPrivate();
		this.publicKey = kp.getPublic();

		this.neighborhood = new Neighborhood(this);
		this.gossiper = new Gossiper(this);
		this.sendQ = new MessageSendQueue(this);
		this.tcp = new TCPNode(this, port);
	}

	public void start() throws FileNotFoundException, IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		this.neighborhood.start();
		this.gossiper.start();
		this.sendQ.start();
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

		boolean imTheRecipient = msg.routingInfo.recipient().equals(name.get());

		if (imTheRecipient) {
			var from = neighborhood.findPeerByName(msg.routingInfo.source());
			byte[] decryptedE2E = NetworkBox.decrypt(this.privateKey, from.publicKey, msg.content);
//			msg.content = decryptedE2E;
			Object contentObject = ByUtils.serializer.fromBytes(decryptedE2E);

			System.out.println("*** message received: " + msg);
			System.out.println("*** content: " + contentObject);

			var recipientNode = g().indexes.byId.get(msg.recipient);

			if (recipientNode != null) {
				recipientNode.onNewMessage(msg, contentObject);
			} else {
				System.err.println("Warning: No recipient node found for message " + msg);
			}
		} else {
			sendQ.considerForwarding(msg, null);
		}
	}
}
