package byransha.graph;

import byransha.graph.action.ActionResult;
import byransha.nodes.system.ChatNode;

public class OpenInNewChat extends NodeAction<BNode, BNode> {

	public OpenInNewChat(BGraph g, BNode inputNode) {
		super(g, inputNode, node.class);
	}

	@Override
	public String whatItDoes() {
		return "open in a new chat";
	}

	@Override
	public ActionResult<BNode, BNode> exec(ChatNode chat) throws Throwable {
		var newChat = new ChatNode(currentUser());
		return createResultNode(inputNode, true);
	}

	@Override
	public boolean applies(ChatNode chat) {
		return true;
	}

}
