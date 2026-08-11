package byransha.lab.device;

import byransha.Element;
import byransha.ID;
import byransha.action.base.ShowInKishanView;
import byransha.lab.LabNode;
import byransha.primitive.DateNode;
import byransha.primitive.Document;
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
