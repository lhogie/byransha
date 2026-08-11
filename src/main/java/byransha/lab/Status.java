package byransha.lab;

import byransha.ID;
import byransha.graph.Element;
import byransha.graph.LabNode;
import byransha.graph.ShowInKishanView;
import byransha.primitive.StringNode;

public class Status extends LabNode {
	@ShowInKishanView
	StringNode name= lookupOrCreate("name",  id ->new StringNode(this, id, null, ".+"));

	public Status(Element g, ID id) {
		super(g, id);
	}

	@Override
	public String whatIsThis() {
		return "a position status defined by the employeer";
	}

	@Override
	public String toString() {
		return name.toString();
	}
}
