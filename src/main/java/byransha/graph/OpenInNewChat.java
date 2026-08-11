package byransha.graph;

import byransha.graph.Category.node;
import byransha.system.ChatNode;

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
		var newChat = new ChatNode(hub().currentUser());
	}

	@Override
	public boolean applies() {
		return true;
	}

}
