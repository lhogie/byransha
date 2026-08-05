package byransha.network;

import java.lang.management.ManagementFactory;

import byransha.graph.ServiceNode;
import byransha.graph.ShowInKishanView;
import byransha.primitive.BooleanNode;
import byransha.primitive.DoubleNode;
import byransha.util.ByUtils;

public class Gossiper extends ServiceNode {

	@ShowInKishanView
	final BooleanNode active = new BooleanNode(this, true);

	@ShowInKishanView
	final DoubleNode periodS = new DoubleNode(this, 5);

	public Gossiper(NetworkAgent net) {
		super(net);
	}

	public void start() {
		ByUtils.loop(() -> periodS.get(), "forward local info (including neighborhood)", () -> {
			if (active.get() && hub().networkAgent != null) {
				var gossip = new PeerInfo();
				var neighbors = hub().networkAgent.neighborhood.neighbors();
				gossip.aiTelemetry = new PeerTelemetry();
				gossip.uptimeMs = ManagementFactory.getRuntimeMXBean().getUptime();
				gossip.neighborsName = Peer.neighborsNames(neighbors);
				gossip.systemProperties = System.getProperties();
				hub().networkAgent.messageOutQueue.send(neighbors, msg -> msg.plainData.content = gossip);
			}
		});
	}
}
