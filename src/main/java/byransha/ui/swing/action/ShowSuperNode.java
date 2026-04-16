package byransha.ui.swing.action;

import byransha.graph.BGraph;
import byransha.graph.Category.chatpanel;
import byransha.graph.list.action.FunctionAction;
import byransha.ui.swing.ChatPanelNode;

public class ShowSuperNode extends FunctionAction<ChatPanelNode, BGraph> {

	public ShowSuperNode(ChatPanelNode inputNode) {
		super(inputNode, chatpanel.class);
	}

	@Override
	public String whatItDoes() {
		return "show super node";
	}

	@Override
	protected void impl() throws Throwable {
		result = g;
	}

	@Override
	public boolean applies() {
		return true;
	}

}
