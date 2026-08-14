package lab.device;

import byransha.Element;
import byransha.ID;
import byransha.action.base.ShowInKishanView;
import byransha.primitive.DateNode;
import byransha.primitive.Document;
import byransha.primitive.StringNode;
import lab.LabElement;

public class Invoice extends LabElement {
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
