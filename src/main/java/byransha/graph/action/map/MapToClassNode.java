package byransha.graph.action.map;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.relection.ClassNode;
import byransha.nodes.primitive.ListNode;

public class MapToClassNode extends AbstractMapAction {

	public MapToClassNode(BGraph g, ListNode l) {
		super(g, l);
	}

	@Override
	protected BNode map(BNode n) {
		return g.i.byClass.findFirstOr(ClassNode.class, cn -> cn.clazz == n.getClass(),
				() -> new ClassNode(g, n.getClass()));
	}

	@Override
	public String mapTo() {
		return "class node";
	}
}