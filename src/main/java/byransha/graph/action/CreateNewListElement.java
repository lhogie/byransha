package byransha.graph.action;

import byransha.ID;
import byransha.graph.Category.list;
import byransha.graph.Element;
import byransha.graph.ProcedureAction;
import byransha.graph.ShowInKishanView;
import byransha.graph.list.action.ListNode;
import byransha.graph.relection.ClassNode;
import byransha.system.Byransha;

public class CreateNewListElement extends ProcedureAction<ListNode> {

	@ShowInKishanView
	ListNode<ClassNode> candidateClasses = new ListNode<>(this, null, "business class(es)", ClassNode.class);
	private final Element newNodeParent;

	public CreateNewListElement(ListNode list, Element pp) {
		super(list, list.class);
		this.newNodeParent = pp;
		candidateClasses.elements.addAll(hub().classesIn(Byransha.class.getPackage(), list.contentClass));
	}

	@Override
	public boolean applies() {
		return true;
	}

	@Override
	public String toString() {
		return "list element creator";
	}

	@Override
	public String whatItDoes() {
		return "creates a new element in the list";
	}

	@Override
	public void impl() {
		var list = inputNode.get();

		for (var c : candidateClasses.getSelected()) {
			list.add(c.newInstance(newNodeParent, new ID()));
		}
	}
}
