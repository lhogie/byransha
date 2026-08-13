package byransha.network;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import byransha.Element;
import byransha.action.base.ShowInKishanView;
import byransha.primitive.LongNode;
import byransha.security.NetworkBox;
import byransha.security.PublicKeyImporter;
import byransha.service.system.Hub;
import byransha.util.ByUtils;

public class Network extends Element {

	protected long nbMsgReceived;

	@ShowInKishanView
	final LongNode receptionInfo = new LongNode(this, null, 0);

	@ShowInKishanView
	public final Sender sender;

	@ShowInKishanView
	public final PeerManager neighborhood;

	@ShowInKishanView
	public final TCPNode tcp;

	@ShowInKishanView
	public final PublicKeyImporter publicKeyImporter = new PublicKeyImporter(this);

	public Network(Hub g, int port)
			throws FileNotFoundException, IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		super(g, null);
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
		receptionInfo.set(nbMsgReceived);
	}

	public synchronized void processIncomingMessage(byte[] wireMsg, Peer p) {
		if (p.sharedSecret == null) {
			System.out.println("Ignoring packet from " + p.name + ": missing public key/shared secret.");
			return;
		}

		byte[] hopDecrypted = NetworkBox.decryptFast(p.sharedSecret, wireMsg);
		Message.ToSerialize toSer = (Message.ToSerialize) ByUtils.serializer.fromBytes(hopDecrypted);
		Message msg = new Message(null, toSer.messageID);
		msg.setSer(toSer, hub());
		msg.actualRoute.add(p);

		++nbMsgReceived;
		updateInOutInfo();

		Peer src = msg.sender();

		boolean imTheRecipient = msg.recipient() == neighborhood.self;

		if (imTheRecipient) {
			System.out.println("*** message received from " + src + " (sender: " + msg.actualRoute + ")");

			byte[] decryptedE2E = NetworkBox.decrypt(neighborhood.self.privateKey, src.publicKey, msg.contentBytes());
//			msg.content = decryptedE2E;
			msg.content = ByUtils.serializer.fromBytes(decryptedE2E);

			System.out.println("*** message received: " + msg);
			System.out.println("*** content: " + msg.content);

			var recipientQ = (MessageQ) hub().indexes.byId.get(msg.recipientQueueAtDestination());

			if (recipientQ != null) {
				recipientQ.q.add_sync(msg);
			} else {
				System.err.println("Warning: No recipient node found for message " + msg);
			}
		} else {
			System.out.println("forwarding " + msg);
			sender.considerForwarding(msg, null);
		}
	}
}
