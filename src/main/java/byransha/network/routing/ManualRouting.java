package byransha.network.routing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import byransha.action.base.ShowInKishanView;
import byransha.network.Peer;
import byransha.network.Sender;

// pick a random neighbor as relay
public class ManualRouting extends RoutingService {
	@ShowInKishanView
	public Map<Peer, Peer> table = new HashMap<>();

	public ManualRouting(Sender s) {
		super(s);
	}

	@Override
	public List<Peer> findRelaysToReach(Peer destination) {
		var relay = table.get(destination);

		if (relay == null) {
			return null;
		} else {
			return List.of(relay);
		}
	}

	@Override
	public void start() {
	}

}
