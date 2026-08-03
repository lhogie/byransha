package byransha.network;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import byransha.event.Event;
import byransha.graph.Ack;
import byransha.graph.PublicKeyImporter;
import byransha.graph.Root;
import byransha.graph.ServiceNode;
import byransha.graph.ShowInKishanView;
import byransha.nodes.primitive.StringNode;
import byransha.util.ByUtils;

public class NetworkAgent extends ServiceNode {
	protected int nbMsgReceived;

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

	@Override
	public synchronized void onNewMessage(Message msg) {
		System.out.println("*** message received: " + msg);
		++nbMsgReceived;
		updateInOutInfo();

		var from = neighborhood.findPeerByName(msg.routingInfo.source());
		boolean imTheRecipient = msg.routingInfo.recipient().equals(name);

		if (imTheRecipient) {
			var content = ByUtils.serializer.fromBytes(msg.content);

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
			} else if (content instanceof PeerInfo e) {
				from.lastInfo = e;
				from.neighbors = e.neighborsName.stream().map(name -> {
					var peer = neighborhood.findPeerByName(name);

					if (peer == null) {
						peer = new Peer(g());
						peer.name = name;
						neighborhood.peers.elements.add(peer);
					}
					return peer;
				}).toList();
				sendQ.considerForwarding(msg, null);
			} else {
				var service = g().indexes.byId.get(msg.recipient);

				if (service != null) {
				} else {
					service.onNewMessage(msg);
				}
			}
		} else {
			sendQ.considerForwarding(msg, null);
		}
	}

}
