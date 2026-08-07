package byransha.network.routing;

import java.util.List;

import byransha.network.Peer;
import byransha.network.Sender;
import byransha.system.SystemNode;

public abstract class RoutingService extends SystemNode {

	public RoutingService(Sender net) {
		super(net);
	}

	public abstract List<Peer> computeRouteToReach(Peer destination);

}
