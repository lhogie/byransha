package byransha.index;

import byransha.Element;
import byransha.action.base.ShowInKishanView;
import byransha.graph.relection.ClassNode;
import byransha.list.action.ListNode;
import byransha.primitive.LongNode;
import byransha.service.system.Hub;

public class AllIndexesElement extends Element {
	public AllIndexesElement(Hub g) {
		super(g, null);
	}

	@ShowInKishanView
	public final LongNode nbClasses() {
		return new LongNode(this, null, (long) hub().indexes.byClass.m.keys().size());
	}

	@ShowInKishanView
	public ListNode<ClassNode> classes() {
		ListNode<ClassNode> r = new ListNode<ClassNode>(this, null, "classes", ClassNode.class);
		hub().indexes.byClass.m.get(ClassNode.class).forEach(c -> r.elements.add((ClassNode) c));
		r.elements.sort((a, b) -> a.toString().compareTo(b.toString()));
		return r;
	}

	@Override
	public String whatIsThis() {
		return "node indexes";
	}

	@Override
	public String toString() {
		return "indexes";
	}

}
