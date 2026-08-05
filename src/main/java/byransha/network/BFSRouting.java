package byransha.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import byransha.graph.BNode;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

public class BFSRouting extends RoutingService {

	public BFSRouting(Sender net) {
		super(net);
	}

	@Override
	public List<Peer> computeRouteToReach(Peer destination) {
		var predecessors = bfs(hub().networkAgent.neighborhood.self);
		System.out.println(predecessors);
		List<Peer> r = new ArrayList<Peer>();

		while (true) {
			var pred = predecessors.get(destination);

			if (pred == null)
				return null;

			r.add(pred);

			if (pred == hub().networkAgent.neighborhood.self)
				break;

			destination = pred;
		}

		Collections.reverse(r);
		return r;
	}

	public Object2ObjectOpenHashMap<Peer, Peer> bfs(Peer source) {
		List<Peer> q = new ArrayList<>();
		var preds = new Object2ObjectOpenHashMap<Peer, Peer>();
		Set<BNode> visited = new HashSet<>();

		q.add(hub().networkAgent.neighborhood.self);

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
