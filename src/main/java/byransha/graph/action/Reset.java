package byransha.graph.action;

import byransha.graph.BNode;
import byransha.graph.Category.node;
import byransha.graph.ProcedureAction;
import byransha.nodes.system.SystemNode;

final public class Reset extends ProcedureAction<BNode> {
	public Reset(BNode n) {
		super(n, node.class);
	}

	@Override
	public String whatItDoes() {
		return "reset the values";
	}

	@Override
	public void impl() {
		inputNode.reset();
	}

	@Override
	public boolean applies() {
		return !(inputNode instanceof SystemNode);
	}
}