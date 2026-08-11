package byransha.network;

import java.util.function.Consumer;

import byransha.ID;
import byransha.graph.Element;
import byransha.graph.LoopingThreadNode;
import byransha.graph.ShowInKishanView;
import byransha.primitive.BooleanNode;
import byransha.primitive.DoubleNode;

public abstract class Gossiper extends Element implements Consumer<Message> {

	@ShowInKishanView
	final BooleanNode active = new BooleanNode(this, null, true);

	@ShowInKishanView
	final DoubleNode periodS = new DoubleNode(this, null, 5);

	@ShowInKishanView
	public Object gossip;

	public Gossiper(Element parent, double period) {
		super(parent, null);
		periodS.set(period);
	}

	public void start() {
		MessageQ q = new MessageQ(this, new ID(0, getClass().hashCode()));
		new LoopingThreadNode(this, () -> 0d, "read gossips", () -> accept(q.q.poll_sync()));

		new LoopingThreadNode(this, () -> periodS.get(), "forward local info (including neighborhood)", () -> {
			if (active.get() && hub().network != null) {
				for (var neighbor : hub().network.neighborhood.neighbors()) {
					var msg = createNewMessage();
					msg.ooInfos.recipient = neighbor;
					msg.recipientQueueAtDestination = q.id();
					msg.ooInfos.content = gossip = createGossip();
					hub().network.sender.accept(msg);
				}
			}
		});
	}

	protected abstract Object createGossip();
}
