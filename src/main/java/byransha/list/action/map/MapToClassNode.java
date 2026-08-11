package byransha.list.action.map;

import byransha.Element;
import byransha.action.Category;
import byransha.action.Category.node;
import byransha.graph.relection.ClassNode;
import byransha.list.action.ListNode;

public class MapToClassNode<IN extends Element> extends AbstractMapAction<IN, ClassNode> {

	public MapToClassNode(ListNode<IN> l) {
		super(l, node.class, map.class);
	}

	public static class map extends Category {
	}

	@Override
	protected ClassNode map(IN n) {
		return hub().indexes.byClass.getClassNodeFor(n.getClass());
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