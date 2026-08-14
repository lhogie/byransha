package lab;

import byransha.InstantiationParameters;
import byransha.primitive.StringNode;

public class SupportDePoste extends LabElement {
	StringNode id = fieldNode("id", id -> new StringNode(this, id, null, ".+"));

	public SupportDePoste(InstantiationParameters p) {
		super(p);
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
