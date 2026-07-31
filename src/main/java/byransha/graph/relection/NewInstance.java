package byransha.graph.relection;

import byransha.graph.BNode;
import byransha.graph.list.action.FunctionAction;
import byransha.graph.relection.LinkAction.type;

public class NewInstance extends FunctionAction<ClassNode, BNode> {
	// @ShowInKishanView
	BNode parent = super.parent;

	public NewInstance(ClassNode inputNode) {
		super(inputNode, type.class);
	}

	@Override
	public String whatItDoes() {
		return "creates a new instance";
	}

	@Override
	public void impl() {
		result = inputNode.newInstance(parent);
	}

	@Override
	public boolean applies() {
		return true;
	}

}
