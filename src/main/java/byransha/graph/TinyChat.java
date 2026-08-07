package byransha.graph;

import byransha.graph.list.action.ListNode;
import byransha.network.Message;
import byransha.network.MessageNode;
import byransha.network.MessageQ;
import byransha.primitive.StringNode;

public class TinyChat extends ServiceNode {
	@ShowInKishanView
	public final StringNode input = new StringNode(this);
	@ShowInKishanView
	public final ListNode<MessageNode> incomingMessages = new ListNode(this, "messages", MessageNode.class);
	private final MessageQ q;

	public TinyChat(Hub g) {
		super(g);

		this.q = new MessageQ(this, 6538776544355L);

		new LoopingThreadNode(this, () -> 1.0, "TinyChat message processing", () -> {
			Message msg = q.q.poll_sync();
			System.out.println("tiny chat recieved message: " + msg.content);
			var n = new MessageNode(hub(), msg);
			incomingMessages.elements.add(n);
		});
	}

	@Override
	public String whatIsThis() {
		return "a tiny chat";
	}

	@ActionMethod
	@AddButtonOnKishanView
	public void sendToNeighbors() {
		for (var neighbor : hub().network.neighborhood.neighbors()) {
			System.out.println("tinychat: sending to " + neighbor);
			var msg = new Message();
			msg.ooInfos.recipient = neighbor;
			msg.recipientNode = q.id();
			msg.ooInfos.content = input.get();
			hub().network.sender.accept(msg);
		}
	}

}
