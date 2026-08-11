package byransha.action;

import byransha.Element;

public abstract class ProcedureAction<IN extends Element> extends Action {

	protected final IN inputNode;

	public ProcedureAction(IN inputNode, Class<? extends Category>... category) {
		super(inputNode, category);
		this.inputNode = inputNode;
	}
}
