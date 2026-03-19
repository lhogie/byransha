package byransha.nodes.system;

import byransha.graph.NodeAction;
import byransha.graph.action.ActionResult;
import byransha.ia.JSONNode;

public class Export extends NodeAction<ChatNode, JSONNode> {

	public Export(ChatNode chatNode) {
		super(chatNode.g, chatNode);
	}

	@Override
	public String whatItDoes() {
		return "export history to JSON";
	}

	@Override
	public ActionResult<ChatNode, JSONNode> exec(ChatNode chat) throws Throwable {
		var json = new JSONNode(g, inputNode.export());
		return createResultNode(json, true);
	}

	@Override
	public boolean applies(ChatNode chat) {
		return true;
	}

}
