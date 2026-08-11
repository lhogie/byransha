package byransha.graph.list.action;

import byransha.graph.Element;
import byransha.graph.Category;
import byransha.graph.ProcedureAction;

public abstract class FunctionAction<IN extends Element, OUT extends Element> extends ProcedureAction<IN> {
	public OUT result;

	public FunctionAction(IN inputNode, Class<? extends Category>... category) {
		super(inputNode, category);
	}
}
