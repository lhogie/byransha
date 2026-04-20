package byransha.graph.relection;

import byransha.graph.BNode;
import byransha.graph.list.action.FunctionAction;
import byransha.graph.list.action.ListNode;
import byransha.graph.relection.LinkAction.type;

public class ShowInstances extends FunctionAction<ClassNode, ListNode<BNode>> {

	public ShowInstances(ClassNode inputNode) {
		super(inputNode, type.class);
	}

	@Override
	public String whatItDoes() {
		return "show instances";
	}

	@Override
	public void impl() throws Throwable {
		result = new ListNode<>(parent, "all instances", BNode.class);
		result.elements.addAll(inputNode.allInstances().elements);
	}

	@Override
	public boolean applies() {
		return true;
	}

}
