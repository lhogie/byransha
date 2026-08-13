package byransha.service.misc;

import byransha.Service;
import byransha.action.ActionMethod;
import byransha.action.AddButtonOnKishanView;
import byransha.action.base.ShowInKishanView;
import byransha.list.action.ListNode;
import byransha.network.Message;
import byransha.primitive.StringNode;
import byransha.service.system.Hub;

public class TinyChat extends Service {
	@ShowInKishanView
	public final StringNode input = new StringNode(this, null, "", ".*");

	@ShowInKishanView
	public final StringNode recipient = new StringNode(this, null, "", ".*");

	@ShowInKishanView
	public final ListNode<Message> incomingMessages = new ListNode(this, null, "messages", Message.class);

	public TinyChat(Hub g) {
		super(g);
	}

	@Override
	public String whatIsThis() {
		return "a tiny chat";
	}

	@ActionMethod
	@AddButtonOnKishanView
	public void send() {
		if (recipient.get().isEmpty()) {
			for (var neighbor : hub().network.neighborhood.neighbors()) {
				System.out.println("tinychat: sending to " + neighbor);
				var msg = createNewMessage();
				msg.recipient = neighbor;
				msg.content = input.get();
				hub().network.sender.accept(msg);
			}
		} else {
			System.out.println("tinychat: sending to " + recipient.get());
			var msg = createNewMessage();
			msg.recipient = hub().network.neighborhood.findPeerByName(recipient.get());
			msg.content = input.get();
			hub().network.sender.accept(msg);
		}
	}

	@Override
	protected void incomingMessage(Message msg) {
		System.out.println("tiny chat recieved message: " + msg.content);
		incomingMessages.elements.add(msg);
	}
}
