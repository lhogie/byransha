package byransha.graph.action.list;

import java.util.Comparator;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.Category;
import byransha.graph.action.list.ListNode.list;
import byransha.nodes.system.ChatNode;

public abstract class Sort extends FilterAction<ListNode> implements Comparator<BNode> {

	public static class sort extends Category {
	}

	public Sort(BGraph g, ListNode inputNode) {
		super(g, inputNode, list.class, sort.class);
	}

	@Override
	public String whatItDoes() {
		return "sort by " + sortBy();
	}

	protected abstract String sortBy();

	@Override
	protected void apply(ListNode list) {
		list.elements.sort(this);
	}

	@Override
	public boolean applies(ChatNode chat) {
		return !inputNode.elements.isEmpty();
	}

}
