package byransha.network.routing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import byransha.network.Peer;
import byransha.network.Sender;

// pick a random neighbor as relay
public class FixedRouting extends RoutingService {
	public Map<Peer, Peer> table = new HashMap<>();

	public FixedRouting(Sender s) {
		super(s);
	}

	@Override
	public List<Peer> computeRouteToReach(Peer destination) {
		var relay = table.get(destination);

		if (relay == null) {
			return null;
		} else {
			return List.of(relay);
		}
	}
}
