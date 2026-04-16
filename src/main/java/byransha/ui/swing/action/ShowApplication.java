package byransha.ui.swing.action;

import byransha.graph.BNode;
import byransha.graph.Category.chatpanel;
import byransha.graph.list.action.FunctionAction;
import byransha.ui.swing.ChatPanelNode;

public class ShowApplication extends FunctionAction<ChatPanelNode, BNode> {

	public ShowApplication(ChatPanelNode inputNode) {
		super(inputNode, chatpanel.class);
	}

	@Override
	public String whatItDoes() {
		return "home";
	}

	@Override
	protected void impl() throws Throwable {
		result = g.application;
	}

	@Override
	public boolean applies() {
		return true;
	}

}
