package byransha.system;

import byransha.graph.Element;
import byransha.graph.Hub;
import byransha.graph.ShowInKishanView;
import byransha.graph.list.action.ListNode;
import byransha.primitive.StringNode;

public class User extends Element {
	@ShowInKishanView
	public final StringNode name;
	@ShowInKishanView
	public final StringNode passwordNode = new StringNode(this, null, null, ".+");
	@ShowInKishanView
	public final ListNode<ChatNode> chats = new ListNode<>(this, null, "chats", ChatNode.class);
	@ShowInKishanView
	public final ListNode<Role> roles = new ListNode<>(this, null, "roles", Role.class);

	public User(Hub g, String userName) {
		super(g, null);
		name = new StringNode(this, null, userName, ".+");
		passwordNode.hideText = true;
	}

	@Override
	public String whatIsThis() {
		return "a user of the system";
	}

	public boolean accept(String username, String p) {
		return name.get().equals(username) && passwordNode.get().equals(p);
	}

	@Override
	public String toString() {
		return name.get();
	}

	public static interface JumpListener {
		void newNode(Element n);
	}

}
