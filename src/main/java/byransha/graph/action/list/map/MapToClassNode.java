package byransha.graph.action.list.map;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.Category;
import byransha.graph.action.list.ListNode;
import byransha.graph.relection.ClassNode;
import byransha.nodes.system.ChatNode;

public class MapToClassNode<IN extends BNode> extends AbstractMapAction<IN, ClassNode> {

	public MapToClassNode(BGraph g, ListNode<IN> l) {
		super(g, l, node.class, map.class);
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
	public boolean applies(ChatNode chat) {
		return inputNode.elements.size() > 0;
	}
}