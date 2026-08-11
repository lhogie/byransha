package byransha;

import byransha.graph.Element;
import byransha.graph.LoopingThreadNode;
import byransha.network.Message;
import byransha.network.MessageQ;

public abstract class Service extends Element {

	public final ID qId = new ID(0, getClass().hashCode());
	private final MessageQ defaultQ;

	protected Service(Element parent) {
		super(parent, null);
		this.defaultQ = createQueue(qId);

		new LoopingThreadNode(this, () -> 1.0, this + " message processing", () -> {
			Message msg = defaultQ.q.poll_sync();
			System.out.println("tiny chat recieved message: " + msg.content);
			incomingMessage(msg);
		});
	}

	protected abstract void incomingMessage(Message msg);

	@Override
	protected Message createNewMessage() {
		var msg = super.createNewMessage();
		msg.recipientQueueAtDestination = defaultQ.id();
		return msg;
	}

	protected MessageQ createQueue(ID id) {
		return new MessageQ(this, id);
	}

}
