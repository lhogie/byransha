package byransha.graph;

import byransha.graph.list.action.ListNode;
import byransha.network.Message;
import byransha.network.MessageNode;
import byransha.nodes.primitive.StringNode;

public class TinyChat extends ServiceNode {
	@ShowInKishanView
	public final StringNode input = new StringNode(this);
	@ShowInKishanView
	public final ListNode<MessageNode> incomingMessages = new ListNode(this, "messages", MessageNode.class);

	public TinyChat(Root g) {
		super(g);
	}

	@Override
	public String whatIsThis() {
		return "a tiny chat";
	}

	@ActionMethod
	@AddButtonOnKishanView
	public void send() {
		g().networkAgent.sendQ.sendObjectToNeighbors(input.get(), msg -> {
			msg.recipient = id();
		});
	}

	@Override
	public void onNewMessage(Message msg, Object content) {
		System.out.println("tiny chat recieved message: " + content);
		var n = new MessageNode(g(), msg);
		incomingMessages.elements.add(n);
	}
}
