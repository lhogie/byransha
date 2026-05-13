package byransha.nodes.lab.device;

import byransha.graph.BNode;
import byransha.graph.DocumentNode;
import byransha.graph.ShowInKishanView;
import byransha.nodes.primitive.DateNode;
import byransha.nodes.primitive.StringNode;

public class Invoice extends BNode {
	@ShowInKishanView
	public Quote quote;
	@ShowInKishanView
	DocumentNode node;
	@ShowInKishanView
	public DateNode date;
	@ShowInKishanView
	public StringNode number;

	protected Invoice(BNode parent) {
		super(parent);
	}
}
