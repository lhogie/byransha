package byransha.network;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import byransha.graph.BNode;
import byransha.graph.ServiceNode;
import byransha.graph.ShowInKishanView;
import byransha.nodes.primitive.StringNode;
import byransha.util.ByUtils;
import byransha.util.Q;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

public class MessageSendQueue extends ServiceNode {
	private Q<Message> sendingBox = new Q<>(1000);

	@ShowInKishanView
	protected int messageSent;

	@ShowInKishanView
	final StringNode sendInfo = new StringNode(this);

	public MessageSendQueue(NetworkAgent net)
			throws FileNotFoundException, IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		super(net);
	}

	public void start() {
		ByUtils.thread("message sending", () -> {
			while (true) {
				Message msg = sendingBox.poll_sync();
				msg.nbAttempts++;

				if (msg.keepAliveExpired())
					continue;

				var recipient = g().networkAgent.neighborhood.findPeerByName(msg.routingInfo.recipient());
				var route = computeRouteToReach(recipient);

				if (route != null) { // if a better route could be found
					msg.routingInfo.suggestedRoute = route.stream().map(p -> p.name).toList();
				}

				var relay = g().networkAgent.neighborhood.findPeerByName(msg.routingInfo.suggestedRoute.getFirst());

				if (relay.getConnection() != null) {
					try {
//System.out.println("sending message to " + relay.name + " via route " + msg.routingInfo.suggestedRoute);
						relay.getConnection().write(msg);
						++messageSent;
						updateInOutInfo();
					} catch (IOException e) {
						msg.errorCount++;

						if (msg.nbAttempts < msg.maxNbAttempts) {
							sendingBox.add_sync(msg);
						}
					}
				} else {
					if (msg.nbAttempts < msg.maxNbAttempts) {
						sendingBox.add_sync(msg);
					}
				}
			}
		});
	}

	List<Peer> computeRouteToReach(Peer destination) {
		var predecessors = bfs();
		List<Peer> r = new ArrayList<Peer>();

		while (true) {
			var pred = predecessors.get(destination);

			if (pred == null)
				break;

			r.add(pred);
			destination = pred;
		}

		Collections.reverse(r);
		return r.isEmpty() ? null : r;
	}

	public Object2ObjectOpenHashMap<Peer, Peer> bfs() {
		List<Peer> q = new ArrayList<>();
		var preds = new Object2ObjectOpenHashMap<Peer, Peer>();
		Set<BNode> visited = new HashSet<>();

		for (Peer p : g().networkAgent.neighborhood.neighbors()) {
			q.add(p);
		}

		while (!q.isEmpty()) {
			Peer p = q.removeFirst();

			for (Peer succ : p.neighbors) {
				if (!visited.contains(succ)) {
					visited.add(succ);
					q.add(succ);
					preds.put(succ, p);
				}
			}
		}

		return preds;
	}

	private void updateInOutInfo() {
		sendInfo.set(messageSent + " sent");
	}

	@ShowInKishanView
	public int nbMessageInSendingQueue() {
		return sendingBox.size();
	}

	public void sendObject(Object o, Peer to, Consumer<Message> c) {
		var msg = new Message();
		msg.routingInfo.suggestedRoute.add(to.name);
		msg.routingInfo.actualRoute.add(g().networkAgent.name.get());
		msg.content = ByUtils.serializer.toBytes(o);

		if (c != null) {
			c.accept(msg);
		}

		sendingBox.add_sync(msg);
	}

	public void send(Object o, Collection<Peer> to, Consumer<Message> c) {
		for (var p : to) {
			sendObject(o, p, c);
		}
	}

	public void sendObjectToNeighbors(Object o, Consumer<Message> c) {
		for (var p : g().networkAgent.neighborhood.neighbors()) {
			sendObject(o, p, c);
		}
	}

	public void considerForwarding(Message msg, Consumer<Message> c) {
		if (msg.routingInfo.actualRoute.contains(g().networkAgent.name.get()))
			return;

		sendingBox.add_sync(msg);
	}

}
