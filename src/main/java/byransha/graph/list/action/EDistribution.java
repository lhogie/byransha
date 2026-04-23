package byransha.graph.list.action;

import byransha.graph.BNode;
import byransha.graph.Category.list;
import byransha.graph.Category.statistics;
import byransha.nodes.lab.stats.DistributionNode;

public class EDistribution<V extends BNode> extends FunctionAction<ListNode<V>, DistributionNode<V>> {

	public EDistribution(ListNode<V> inputNode) {
		super(inputNode, list.class, statistics.class);
	}

	@Override
	public String whatItDoes() {
		return "computes distribution";
	}

	@Override
	public void impl() throws Throwable {
		result = new DistributionNode<V>(null) {

			@Override
			public String toString() {
				return inputNode.label;
			}
		};

		inputNode.get().forEach(e -> result.entries.addOccurence(e, 1));
	}

	@Override
	public boolean applies() {
		return true;
	}
}