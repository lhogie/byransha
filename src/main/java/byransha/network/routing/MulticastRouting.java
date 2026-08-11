package byransha.network.routing;

import java.util.List;

import byransha.network.Peer;
import byransha.network.Sender;

public class MulticastRouting extends RoutingService {
	public MulticastRouting(Sender net) {
		super(net);
	}

	@Override
	public List<Peer> findRelaysToReach(Peer destination) {
		return hub().network.neighborhood.neighbors();
	}

	@Override
	public void start() {
	}
}
