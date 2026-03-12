package byransha.graph;

import byransha.graph.action.ActionResult;
import byransha.nodes.system.ChatNode;

public class OpenInNewChat extends NodeAction<BNode, BNode> {

	public OpenInNewChat(BGraph g, BNode inputNode) {
		super(g, inputNode);
	}

	@Override
	public String whatItDoes() {
		return "opens in a new chat";
	}

	@Override
	public ActionResult exec(ChatNode chat) throws Throwable {
		var newChat = new ChatNode(currentUser(), inputNode);
		return createResultNode(inputNode, true);
	}

	@Override
	public boolean applies(ChatNode chat) {
		return true;
	}

}
