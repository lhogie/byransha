package byransha.graph.relection;

import byransha.Element;
import byransha.graph.relection.LinkAction.type;
import byransha.list.action.FunctionAction;
import byransha.list.action.ListNode;

public class ShowInstances extends FunctionAction<ClassNode, ListNode<Element>> {

	public ShowInstances(ClassNode inputNode) {
		super(inputNode, type.class);
	}

	@Override
	public String whatItDoes() {
		return "show instances";
	}

	@Override
	public void impl() throws Throwable {
		result = new ListNode<>(this, null, "all instances", Element.class);
		result.elements.addAll(inputNode.allInstances().elements);
	}

	@Override
	public boolean applies() {
		return true;
	}

}
