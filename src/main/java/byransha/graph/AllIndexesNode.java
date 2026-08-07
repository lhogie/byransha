package byransha.graph;

import byransha.graph.list.action.ListNode;
import byransha.graph.relection.ClassNode;
import byransha.primitive.LongNode;

public class AllIndexesNode extends BNode {

	@ShowInKishanView
	public final LongNode nbClasses() {
		return new LongNode(this, (long) hub().indexes.byClass.m.keys().size());
	}

	@ShowInKishanView
	public ListNode<ClassNode> classes() {
		ListNode<ClassNode> r = new ListNode<ClassNode>(this, "classes", ClassNode.class);
		hub().indexes.byClass.m.get(ClassNode.class).forEach(c -> r.elements.add((ClassNode) c));
		r.elements.sort((a, b) -> a.toString().compareTo(b.toString()));
		return r;
	}

	protected AllIndexesNode(Hub g) {
		super(g);
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
