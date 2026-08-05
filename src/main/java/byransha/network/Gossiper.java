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
		Queue q = new Queue(this, 3938484L);
		ByUtils.loop(() -> periodS.get(), "forward local info (including neighborhood)", () -> {
			if (active.get() && hub().networkAgent != null) {
				for (var neighbor : hub().networkAgent.neighborhood.neighbors()) {
					var msg = new Message();
					msg.ooInfos.recipient = neighbor;
					msg.recipientNode = q.id();
					msg.ooInfos.content = gossip();
					hub().networkAgent.messageOutQueue.send(msg);
				}
			}
		});
	}

	private Object gossip() {
		var gossip = new PeerInfo();
		gossip.name = ((NetworkAgent) parent).neighborhood.self.name;
		gossip.aiTelemetry = new PeerTelemetry();
		gossip.uptimeMs = ManagementFactory.getRuntimeMXBean().getUptime();
		var neighbors = hub().networkAgent.neighborhood.neighbors();
		gossip.neighborsName = Peer.neighborsNames(neighbors);
		gossip.systemProperties = System.getProperties();

		return gossip;
	}
}
