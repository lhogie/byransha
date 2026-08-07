package byransha.network.routing;

import java.util.List;

import byransha.network.Peer;
import byransha.network.Sender;
import byransha.util.ByUtils;

// pick a random neighbor as relay
public class RandomWalk extends RoutingService {

	public RandomWalk(Sender net) {
		super(net);
	}

	@Override
	public List<Peer> computeRouteToReach(Peer destination) {
		var n = hub().network.neighborhood.neighbors();

		if (n.isEmpty()) {
			return null;
		} else if (n.contains(destination)) {
			return List.of(destination);
		} else {
			var random = n.get(ByUtils.random.nextInt(n.size()));
			return List.of(random);
		}
	}
}
