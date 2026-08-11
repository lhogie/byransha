package byransha.graph.action;

import byransha.graph.Category.node;
import byransha.graph.Element;
import byransha.graph.ProcedureAction;

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