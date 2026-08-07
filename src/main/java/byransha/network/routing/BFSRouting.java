package byransha.network.routing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import byransha.graph.BNode;
import byransha.network.Peer;
import byransha.network.Sender;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

public class BFSRouting extends RoutingService {

	public BFSRouting(Sender net) {
		super(net);
	}

	@Override
	public List<Peer> computeRouteToReach(Peer destination) {
		var predecessors = bfs(hub().network.neighborhood.self);
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

	public Object2ObjectOpenHashMap<Peer, Peer> bfs(Peer source) {
		List<Peer> q = new ArrayList<>();
		var preds = new Object2ObjectOpenHashMap<Peer, Peer>();
		Set<BNode> visited = new HashSet<>();

		q.add(hub().network.neighborhood.self);
		visited.add(hub().network.neighborhood.self);

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
}
