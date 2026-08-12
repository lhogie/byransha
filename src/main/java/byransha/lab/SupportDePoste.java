package byransha.lab;

import byransha.Element;
import byransha.ID;
import byransha.primitive.StringNode;

public class SupportDePoste extends LabElement {
	StringNode id = fieldNode("id", id -> new StringNode(this, id, null, ".+"));

	public SupportDePoste(Element g, ID id) {
		super(g, id);
	}

	@Override
	public String whatIsThis() {
		return "support de poste";
	}

	@Override
	public String toString() {
		return id.toString();
	}

}
