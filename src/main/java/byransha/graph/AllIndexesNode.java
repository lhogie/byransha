package byransha.graph;

import byransha.graph.action.list.ListNode;
import byransha.graph.relection.ClassNode;
import byransha.nodes.lab.DynamicValuedNode;
import byransha.nodes.primitive.LongNode;

public class AllIndexesNode extends BNode {

	final DynamicValuedNode<LongNode> nbClasses = new DynamicValuedNode<>(this) {
		@Override
		public LongNode exec() {
			return new LongNode(this, (long) g.indexes.byClass.m.keys().size());
		}
	};

	final DynamicValuedNode<ListNode<ClassNode>> classes = new DynamicValuedNode<ListNode<ClassNode>>(this) {
		@Override
		public ListNode<ClassNode> exec() {
			ListNode<ClassNode> r = new ListNode<ClassNode>(g, "classes");
			g.indexes.byClass.m.get(ClassNode.class).forEach(c -> r.elements.add((ClassNode) c));
			return r;
		}
	};

	protected AllIndexesNode(BGraph g) {
		super(g);
		nbClasses.readOnly = true;
	}

	@Override
	public String whatIsThis() {
		return "node indexes";
	}

	@Override
	public String prettyName() {
		return "indexes";
	}

}
