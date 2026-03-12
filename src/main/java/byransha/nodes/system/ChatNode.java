package byransha.nodes.system;

import java.util.ArrayList;
import java.util.List;

import byransha.graph.BNode;
import byransha.graph.NodeAction;
import byransha.nodes.primitive.ListNode;
import byransha.nodes.system.User.JumpListener;

public class ChatNode extends ListNode<BNode> {
	public final List<JumpListener> jumpListeners = new ArrayList<>();

	public ChatNode(User user, BNode initialNode) {
		super(user.g, "chat");
		user.chats.add(this);
		add(initialNode);
	}

	public BNode currentNode() {
		return get().isEmpty() ? null : get().getLast();
	}

	@Override
	public void add(BNode n) {
		if (size() > 0 && n == get(size() - 1)) // if same node
			return;

		if (n instanceof NodeAction action && action.execStraightAway) {
			try {
				var result = action.exec(this);
				add(result);

				if (result.jumpStraightAwayToResult) {
					add(result.result);
				}
			} catch (Throwable err) {
				add(error(err, false));
			}
		} else {
			add(n);
		}

		jumpListeners.forEach(l -> l.userJumpedTo(n));
	}

}
