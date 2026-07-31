package byransha.graph;

import java.io.IOException;

import byransha.graph.list.action.ListNode;
import byransha.network.Message;
import byransha.network.MessageNode;
import byransha.nodes.primitive.StringNode;

public class TinyChat extends ServiceNode {
	@ShowInKishanView
	public final StringNode input = new StringNode(this);
	@ShowInKishanView
	public final ListNode<MessageNode> incomingMessages = new ListNode(this, "messages", MessageNode.class);

	public TinyChat(BGraph g) {
		super(g);
	}

	@Override
	public String whatIsThis() {
		return "a tiny chat";
	}

	@ActionMethod
	@AddButtonOnKishanView
	public void send() {
		try {
			g().networkAgent.bcast(input.get(), null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onNewMessage(Message msg) {
		var n = new MessageNode(g(), msg);
		incomingMessages.elements.add(n);
	}
}
