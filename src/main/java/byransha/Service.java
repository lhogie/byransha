package byransha;

import byransha.network.Message;
import byransha.network.MessageQ;
import byransha.thread.LoopingThreadNode;

public abstract class Service extends Element {
	private final MessageQ defaultQ;

	protected Service(Element parent) {
		super(parent, null);
		this.defaultQ = createQueue(getClass().hashCode());

		new LoopingThreadNode(this, () -> 0.0, this + " message processing", () -> {
			Message msg = defaultQ.q.poll_sync();
			System.out.println(this + " received message: " + msg.content);
			incomingMessage(msg);
		});
	}

	protected abstract void incomingMessage(Message msg);

	@Override
	protected Message createNewMessage() {
		var msg = super.createNewMessage();
		msg.ser.recipientQueueAtDestination = defaultQ.id();
		return msg;
	}

	protected MessageQ createQueue(long id) {
		return new MessageQ(this, id);
	}

}
