package byransha.graph;

import byransha.graph.list.action.ListNode;
import byransha.graph.relection.ClassNode;
import byransha.nodes.primitive.LongNode;

public class AllIndexesNode extends BNode {

	@ShowInKishanView
	final LongNode nbClasses() {
		return new LongNode(this, (long) g.indexes.byClass.m.keys().size());
	}

	@ShowInKishanView
	ListNode<ClassNode> classes() {
		ListNode<ClassNode> r = new ListNode<ClassNode>(g, "classes", ClassNode.class);
		g.indexes.byClass.m.get(ClassNode.class).forEach(c -> r.elements.add((ClassNode) c));
		return r;
	}

	protected AllIndexesNode(BGraph g) {
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
