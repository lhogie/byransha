package byransha.network;

import java.util.List;

import byransha.graph.ServiceNode;

public abstract class RoutingService extends ServiceNode {

	public RoutingService(Sender net) {
		super(net);
	}

	public abstract List<Peer> computeRouteToReach(Peer destination);

}
