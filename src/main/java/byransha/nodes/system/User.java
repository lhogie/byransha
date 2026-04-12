package byransha.nodes.system;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.list.action.ListNode;
import byransha.nodes.primitive.StringNode;

public class User extends BNode {
	public final StringNode name;
	public final StringNode passwordNode;
	public final ListNode<ChatNode> chatList;

	public User(BGraph g, String userName) {
		super(g);
		name = new StringNode(g, userName, ".+");
		passwordNode = new StringNode(g, null, ".+");
		passwordNode.hideText = true;
		chatList = new ListNode<>(g, "chats");
	}

	@Override
	public String whatIsThis() {
		return "a user of the system";
	}

	@Override
	public boolean canSee(User user) {
		return user == this;
	}

	@Override
	public boolean canEdit(User user) {
		return user == this;
	}

	public boolean accept(String username, String p) {
		return name.get().equals(username) && passwordNode.get().equals(p);
	}

	@Override
	public String toString() {
		return name.get();
	}

	public static interface JumpListener {
		void newNode(BNode n);
	}

}
