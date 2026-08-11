package byransha.action.base;

import byransha.Chat;
import byransha.Element;
import byransha.action.Category;
import byransha.action.ProcedureAction;
import byransha.action.Category.node;

public class OpenInNewChat extends ProcedureAction<Element> {

	public OpenInNewChat(Element inputNode) {
		super(inputNode, node.class);
	}

	@Override
	public String whatItDoes() {
		return "open in a new chat";
	}

	@Override
	public void impl() throws Throwable {
		var newChat = new Chat(hub().currentUser());
	}

	@Override
	public boolean applies() {
		return true;
	}

}
