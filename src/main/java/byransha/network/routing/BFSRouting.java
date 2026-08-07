package byransha.network.routing;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import byransha.graph.ActionMethod;
import byransha.graph.AddButtonOnKishanView;
import byransha.graph.BNode;
import byransha.graph.ShowInKishanView;
import byransha.network.Gossiper;
import byransha.network.Message;
import byransha.network.OtherPeer;
import byransha.network.Peer;
import byransha.network.Sender;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

public class BFSRouting extends RoutingService {

	@ShowInKishanView
	private Object2ObjectMap<Peer, Peer> predecessors;

	public static record Gossip(String peerName, List<String> neighborsName) implements Serializable {
	}

	public BFSRouting(Sender net) {
		super(net);
	}

	@Override
	public void start() {
		gossiper.start();
	}

	@Override
	public List<Peer> findRelaysToReach(Peer destination) {
		var route = computeRouteToReach(destination);

		if (route == null || route.isEmpty()) {
			return Collections.emptyList();
		} else {
			return List.of(route.getFirst());
		}
	}

	public List<Peer> computeRouteToReach(Peer destination) {
		if (predecessors == null) {
			updateRoutingTable();
		}

//		System.out.println(predecessors);
		List<Peer> r = new ArrayList<Peer>();
		r.add(destination);

		while (true) {
			var pred = predecessors.get(destination);

			if (pred == null)
				return null;

			if (pred == hub().network.neighborhood.self)
				break;

			r.add(destination = pred);
		}

		Collections.reverse(r);
		return r;
	}

	public static Object2ObjectOpenHashMap<Peer, Peer> bfs(Peer source) {
		List<Peer> q = new ArrayList<>();
		var preds = new Object2ObjectOpenHashMap<Peer, Peer>();
		Set<BNode> visited = new HashSet<>();
		q.add(source);
		visited.add(source);

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

	@ShowInKishanView
	public Gossiper gossiper = new Gossiper(this, 5) {
		@Override
		protected Gossip createGossip() {
			String name = hub().network.neighborhood.self.name;
			List<Peer> neighbors = hub().network.neighborhood.neighbors();
			List<String> neighborsName = Peer.neighborsNames(neighbors);
			return new Gossip(name, neighborsName);
		}

		@Override
		public void accept(Message msg) {
			var gossip = (Gossip) msg.ooInfos.content;
			Peer peer = hub().network.neighborhood.findPeerByName(gossip.peerName);

			if (peer == null) {
				try {
					peer = new OtherPeer(hub().network.neighborhood, gossip.peerName);
					hub().network.neighborhood.peers.elements.add(peer);
				} catch (IOException err) {
					err.printStackTrace();
				}
			}

			peer.neighbors = new ArrayList<>(gossip.neighborsName.stream().map(name -> {
				Peer n = hub().network.neighborhood.findPeerByName(name);

				if (n == null) {
					try {
						n = new OtherPeer(hub().network.neighborhood, name);
						hub().network.neighborhood.peers.elements.add(n);
					} catch (IOException err) {
						err.printStackTrace();
					}
				}
				return n;
			}).toList());

			predecessors = null;
			hub().network.sender.considerForwarding(msg, null);
		}
	};

	@ActionMethod
	@AddButtonOnKishanView
	public void invalidateRoutingTable() {
		this.predecessors = null;
	}

	@ActionMethod
	@AddButtonOnKishanView
	public void updateRoutingTable() {
		this.predecessors = bfs(hub().network.neighborhood.self);
	}
}
