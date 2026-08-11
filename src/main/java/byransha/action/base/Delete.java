package byransha.action.base;

import byransha.Element;
import byransha.action.ProcedureAction;
import byransha.action.Category.node;

public class Delete extends ProcedureAction<Element> {

	public Delete(Element node) {
		super(node, node.class);
	}

	@Override
	public String whatItDoes() {
		return "delete from the graph";
	}

	@Override
	public void impl() {
		inputNode.delete();
	}

	@Override
	public boolean applies() {
		return inputNode.id() != null;
	}
}