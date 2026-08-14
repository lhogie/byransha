package byransha.network;

import java.util.function.Consumer;

import byransha.Element;
import byransha.action.base.ShowInKishanView;
import byransha.primitive.BooleanNode;
import byransha.primitive.DoubleNode;
import byransha.thread.LoopingThreadNode;

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
		MessageQ q = new MessageQ(this, getClass().hashCode());
		new LoopingThreadNode(this, () -> 0d, "read gossips", () -> accept(q.q.poll_sync()));

		new LoopingThreadNode(this, () -> periodS.get(), "forward local info (including neighborhood)", () -> {
			if (active.get() && hub().network != null) {
				for (var neighbor : hub().network.neighborhood.neighbors()) {
					var msg = createNewMessage();
					msg.recipient = neighbor;
					msg.ser.recipientQueueAtDestination = q.id();
					msg.content = gossip = createGossip();
					hub().network.sender.accept(msg);
				}
			}
		});
	}

	protected abstract Object createGossip();
}
