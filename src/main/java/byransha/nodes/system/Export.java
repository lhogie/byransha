package byransha.nodes.system;

import byransha.ai.JSONNode;
import byransha.graph.Category;
import byransha.graph.NodeAction;
import byransha.graph.action.ActionResult;
import byransha.graph.action.list.ListNode.export;

public class Export extends NodeAction<ChatNode, JSONNode> {

	public static class chat extends Category{}
	
	public Export(ChatNode chatNode) {
		super(chatNode.g, chatNode, chat.class, export.class);
	}

	@Override
	public String whatItDoes() {
		return "export to JSON";
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
