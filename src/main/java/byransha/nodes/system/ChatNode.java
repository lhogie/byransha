package byransha.nodes.system;

import java.util.ArrayList;
import java.util.List;

import byransha.graph.BNode;
import byransha.graph.NodeAction;
import byransha.graph.action.list.ListNode;
import byransha.nodes.system.User.JumpListener;

public class ChatNode extends ListNode<BNode> {
	public final List<JumpListener> newNodeListeners = new ArrayList<>();

	public ChatNode(User user, BNode initialNode) {
		super(user.g, user + "'s chat");
		user.chats.add(this);
		user.chatListeners.forEach(l -> l.newChat(user, this));
		add(initialNode);
	}

	public BNode currentNode() {
		return get().isEmpty() ? null : get().getLast();
	}

	@Override
	public void add(BNode n) {
		if (size() > 0 && n == get(size() - 1)) // if same node
			return;

		if (n instanceof NodeAction action && action.parameters().isEmpty()) {
			try {
				var result = action.exec(this);
				add(result.jumpStraightAwayToOutNode ? result.outNode : result);
			} catch (Throwable err) {
				add(error(err, false));
			}
		} else {
			super.add(n);
			newNodeListeners.forEach(l -> l.newNode(n));
		}

	}

}
