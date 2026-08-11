package byransha.action.base;

import byransha.Element;
import byransha.action.Category;
import byransha.action.Category.node;
import byransha.graph.relection.ClassNode;
import byransha.list.action.FunctionAction;

public class ShowClassNode extends FunctionAction<Element, ClassNode> {

	public ShowClassNode(Element inputNode) {
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
