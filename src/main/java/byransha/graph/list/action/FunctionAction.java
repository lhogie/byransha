package byransha.graph.list.action;

import byransha.graph.BNode;
import byransha.graph.Category;
import byransha.graph.ProcedureAction;

public abstract class FunctionAction<IN extends BNode, OUT extends BNode> extends ProcedureAction<IN> {
	public OUT result;

	public FunctionAction(IN inputNode, Class<? extends Category>... category) {
		super(inputNode, category);
	}
}
