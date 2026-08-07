package byransha.network;

import java.util.UUID;
import java.util.function.Consumer;

import byransha.graph.BNode;
import byransha.graph.LoopingThreadNode;
import byransha.graph.ShowInKishanView;
import byransha.primitive.BooleanNode;
import byransha.primitive.DoubleNode;
import byransha.system.SystemNode;

public abstract class Gossiper extends SystemNode implements Consumer<Message> {

	@ShowInKishanView
	final BooleanNode active = new BooleanNode(this, true);

	@ShowInKishanView
	final DoubleNode periodS = new DoubleNode(this, 5);

	@ShowInKishanView
	public Object gossip;

	public Gossiper(BNode parent, double period) {
		super(parent);
		periodS.set(period);
	}

	public void start() {
		MessageQ q = new MessageQ(this, new UUID(-1619690253403145037L, 1877711994968385949L));
		new LoopingThreadNode(this, () -> 0d, "read gossips", () -> accept(q.q.poll_sync()));

		new LoopingThreadNode(this, () -> periodS.get(), "forward local info (including neighborhood)", () -> {
			if (active.get() && hub().network != null) {
				for (var neighbor : hub().network.neighborhood.neighbors()) {
					var msg = new Message();
					msg.ooInfos.recipient = neighbor;
					msg.recipientNode = q.id();
					msg.ooInfos.content = gossip = createGossip();
					hub().network.sender.accept(msg);
				}
			}
		});
	}

	protected abstract Object createGossip();
}
