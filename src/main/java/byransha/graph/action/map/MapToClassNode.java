package byransha.graph.action.map;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.relection.ClassNode;
import byransha.nodes.primitive.ListNode;

public class MapToClassNode<A extends BNode> extends AbstractMapAction<A, ClassNode> {

	public MapToClassNode(BGraph g, ListNode<A> l) {
		super(g, l);
	}

	@Override
	protected ClassNode map(A n) {
		return g.indexes.byClass.findFirstOr(ClassNode.class, cn -> cn.clazz == n.getClass(),
				() -> new ClassNode(g, n.getClass()));
	}

	@Override
	public String mapTo() {
		return "class node";
	}

	@Override
	public boolean applies() {
		return inputNode.size() > 0;
	}
}