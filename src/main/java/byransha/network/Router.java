package byransha.network;

import java.util.List;

import byransha.graph.ServiceNode;

public abstract class Router extends ServiceNode {

	public Router(Sender net) {
		super(net);
	}

	public abstract List<Peer> computeRouteToReach(Peer destination);

}
