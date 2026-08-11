package byransha.lab.device;

import byransha.ID;
import byransha.graph.Document;
import byransha.graph.Element;
import byransha.graph.LabNode;
import byransha.graph.ShowInKishanView;
import byransha.primitive.DateNode;
import byransha.primitive.StringNode;

public class Invoice extends LabNode {
	@ShowInKishanView
	public Quote quote;
	@ShowInKishanView
	Document node;
	@ShowInKishanView
	public DateNode date;
	@ShowInKishanView
	public StringNode number;

	protected Invoice(Element parent, ID id) {
		super(parent, id);
	}
}
