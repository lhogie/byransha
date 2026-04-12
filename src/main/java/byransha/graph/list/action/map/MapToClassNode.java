package byransha.graph.list.action.map;

import byransha.graph.BNode;
import byransha.graph.Category;
import byransha.graph.list.action.ListNode;
import byransha.graph.relection.ClassNode;

public class MapToClassNode<IN extends BNode> extends AbstractMapAction<IN, ClassNode> {

	public MapToClassNode( ListNode<IN> l) {
		super(l, node.class, map.class);
	}

	public static class map extends Category{}
	
	@Override
	protected ClassNode map(IN n) {
		return g.indexes.byClass.findFirstOr(ClassNode.class, cn -> cn.representedClass == n.getClass(), null);
	}

	@Override
	public String mapTo() {
		return "class node";
	}

	@Override
	public boolean applies() {
		return inputNode.elements.size() > 0;
	}
}