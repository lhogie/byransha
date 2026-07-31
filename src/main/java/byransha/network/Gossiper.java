package byransha.network;

import java.lang.management.ManagementFactory;

import byransha.graph.ServiceNode;
import byransha.graph.ShowInKishanView;
import byransha.nodes.primitive.BooleanNode;
import byransha.nodes.primitive.DoubleNode;
import byransha.util.ByUtils;

public class Gossiper extends ServiceNode {

	@ShowInKishanView
	final BooleanNode active = new BooleanNode(this, true);

	@ShowInKishanView
	final DoubleNode periodS = new DoubleNode(this, 1);

	public Gossiper(NetworkAgent net) {
		super(net);

		ByUtils.thread("forward local info (including neighborhood)", () -> {
			while (true) {
				if (active.get() && g().networkAgent != null) {
					var i = new PeerInfo();
					var neighbors = g().networkAgent.neighborhood.neighbors();
					i.aiTelemetry = new PeerTelemetry();
					i.uptimeMs = ManagementFactory.getRuntimeMXBean().getUptime();
					i.neighborsName = Peer.neighborsNames(neighbors);
					i.systemProperties = System.getProperties();
					g().networkAgent.sendQ.send(i, neighbors, null);
				}

				sleep(periodS.get());
			}
		});
	}
}
