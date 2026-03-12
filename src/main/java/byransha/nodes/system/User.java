package byransha.nodes.system;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.nodes.primitive.ListNode;
import byransha.nodes.primitive.StringNode;

public class User extends BNode {
	public final StringNode name;
	public final StringNode passwordNode;
	public final StringNode argon2Hash;
	public final ListNode<ChatNode> chats;

	public User(BGraph g, String userName, String passwordHash) {
		super(g);
		name = new StringNode(g, userName, ".+");
		passwordNode = new StringNode(g, null, ".+");
		this.argon2Hash = new StringNode(g, passwordHash, ".*");
		passwordNode.hideText = true;
		chats = new ListNode<>(g, "chats");
		chats.get().add(new ChatNode(this, g));

		passwordNode.changeListeners.add(n -> argon2Hash.set(Argon.hash(passwordNode.get())));
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
	public String prettyName() {
		return name.get();
	}

	public static interface JumpListener {
		void userJumpedTo(BNode n);
	}

}
