package byransha.graph;

import byransha.graph.list.action.ListNode;
import byransha.graph.relection.ClassNode;
import byransha.primitive.LongNode;

public class AllIndexesElement extends Element {
	protected AllIndexesElement(Hub g) {
		super(g, null);
	}
	@ShowInKishanView
	public final LongNode nbClasses() {
		return new LongNode(this, null,(long) hub().indexes.byClass.m.keys().size());
	}

	@ShowInKishanView
	public ListNode<ClassNode> classes() {
		ListNode<ClassNode> r = new ListNode<ClassNode>(this,null, "classes", ClassNode.class);
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
