package byransha.network.routing;

import java.util.List;

import byransha.graph.Element;
import byransha.network.Peer;
import byransha.network.Sender;

public abstract class RoutingService extends Element {

	public RoutingService(Sender net) {
		super(net, null);
	}

	public abstract List<Peer> findRelaysToReach(Peer destination);

	public abstract void start();

	public RoutingInfo createInfo() {
		return new RoutingInfo();
	}

}
