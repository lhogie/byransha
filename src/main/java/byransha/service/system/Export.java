package byransha.service.system;

import byransha.Chat;
import byransha.action.Category;
import byransha.ai.JSONNode;
import byransha.list.action.FunctionAction;

public class Export extends FunctionAction<Chat, JSONNode> {

	public Export(Chat inputNode) {
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
		result = new JSONNode(this, inputNode.export(), null);
	}
}
