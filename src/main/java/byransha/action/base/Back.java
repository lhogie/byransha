package byransha.action.base;

import byransha.Chat;
import byransha.action.ProcedureAction;
import byransha.action.base.FreezingAction.misc;

final public class Back extends ProcedureAction<Chat> {
	public Back(Chat n) {
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