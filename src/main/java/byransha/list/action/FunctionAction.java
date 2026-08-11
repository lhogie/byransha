package byransha.list.action;

import byransha.Element;
import byransha.action.Category;
import byransha.action.ProcedureAction;

public abstract class FunctionAction<IN extends Element, OUT extends Element> extends ProcedureAction<IN> {
	public OUT result;

	public FunctionAction(IN inputNode, Class<? extends Category>... category) {
		super(inputNode, category);
	}
}
