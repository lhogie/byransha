package byransha.graph.action;

import byransha.graph.ProcedureAction;
import byransha.graph.action.FreezingAction.misc;
import byransha.nodes.system.ChatNode;

final public class Back extends ProcedureAction<ChatNode> {
	public Back(ChatNode n) {
		super(n, misc.class);
	}

	@Override
	public String whatItDoes() {
		return "back in history";
	}

	@Override
	public void impl() {
		var h = chat.nodes.elements;
		h.remove(h.size() - 1);
	}

	@Override
	public boolean applies() {
		return chat.nodes.elements.size() > 1;
	}

}