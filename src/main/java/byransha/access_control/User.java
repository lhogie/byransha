package byransha.access_control;

import byransha.Chat;
import byransha.Element;
import byransha.action.base.ShowInKishanView;
import byransha.list.action.ListNode;
import byransha.primitive.StringNode;
import byransha.service.system.Hub;

public class User extends Element {
	@ShowInKishanView
	public final StringNode name;
	@ShowInKishanView
	public final StringNode passwordNode = new StringNode(this, null, null, ".+");
	@ShowInKishanView
	public final ListNode<Chat> chats = new ListNode<>(this, null, "chats", Chat.class);
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
