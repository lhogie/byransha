package byransha.nodes.system;

import byransha.ai.JSONNode;
import byransha.graph.Category;
import byransha.graph.list.action.FunctionAction;

public class Export extends FunctionAction<ChatNode, JSONNode> {

	public Export(ChatNode inputNode) {
		super(inputNode, chat.class);
	}

	public static class chat extends Category {
	}

	@Override
	public String whatItDoes() {
		return "export to JSON";
	}

	@Override
	public boolean applies() {
		return true;
	}

	@Override
	public void impl() {
		result = new JSONNode(parent, inputNode.export());
	}
}
