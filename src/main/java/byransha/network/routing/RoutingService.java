package byransha.network.routing;

import java.util.List;

import byransha.graph.ServiceNode;
import byransha.network.Peer;
import byransha.network.Sender;

public abstract class RoutingService extends ServiceNode {

	public RoutingService(Sender net) {
		super(net);
	}

	public abstract List<Peer> computeRouteToReach(Peer destination);

}
