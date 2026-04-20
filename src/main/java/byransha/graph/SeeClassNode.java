package byransha.graph;

import byransha.graph.Category.node;
import byransha.graph.list.action.FunctionAction;
import byransha.graph.relection.ClassNode;

public class SeeClassNode extends FunctionAction<BNode, ClassNode> {

	public SeeClassNode(BNode inputNode) {
		super(inputNode, node.class);
	}

	@Override
	public String whatItDoes() {
		return "show the class node for this node";
	}

	@Override
	public void impl() throws Throwable {
		result = inputNode.type();
	}

	@Override
	public boolean applies() {
		return true;
	}

}
