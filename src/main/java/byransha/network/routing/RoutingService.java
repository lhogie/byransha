package byransha.network.routing;

import java.util.List;

import byransha.Element;
import byransha.network.Message;
import byransha.network.Peer;
import byransha.network.Sender;

public abstract class RoutingService extends Element {

	public RoutingService(Sender net) {
		super(net, null);
	}

	public abstract List<Peer> findRelaysToReach(Peer destination);

	public abstract void start();

	public RoutingInfo createInfoFor(Message msg) {
		return new RoutingInfo(msg);
	}

}
