package byransha.list.action;

import byransha.Element;
import byransha.action.Category.list;
import byransha.action.Category.statistics;
import byransha.lab.stats.DistributionNode;

public class EDistribution<V extends Element> extends FunctionAction<ListNode<V>, DistributionNode<V>> {

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