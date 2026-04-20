package byransha.graph;

import byransha.graph.Category.node;
import byransha.nodes.system.ChatNode;

public class OpenInNewChat extends ProcedureAction<BNode> {

	public OpenInNewChat(BNode inputNode) {
		super(inputNode, node.class);
	}

	@Override
	public String whatItDoes() {
		return "open in a new chat";
	}

	@Override
	public void impl() throws Throwable {
		var newChat = new ChatNode(currentUser());
	}

	@Override
	public boolean applies() {
		return true;
	}

}
