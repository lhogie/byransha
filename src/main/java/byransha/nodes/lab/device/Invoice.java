package byransha.nodes.lab.device;

import byransha.graph.BNode;
import byransha.graph.DocumentNode;

public class Invoice extends BNode {
	public Quote quote;
	DocumentNode node;

	protected Invoice(BNode parent) {
		super(parent);
	}
}
