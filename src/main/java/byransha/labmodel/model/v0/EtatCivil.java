package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.BNode;
import byransha.StringNode;

public class EtatCivil extends BNode {
	public StringNode firstName = new StringNode(graph, null);
	public StringNode name = new StringNode(graph, null);
	public StringNode familyNameBeforeMariage = new StringNode(graph, null);
	public StringNode birthDate = new StringNode(graph, null);
	public StringNode cityOfBirth = new StringNode(graph, null);
	public StringNode nationality = new StringNode(graph, null);
	public StringNode countryOfBirth = new StringNode(graph, null);
	public StringNode address = new StringNode(graph, null);

	public EtatCivil(BBGraph g) {
		super(g);
	}

	public EtatCivil(BBGraph g, int id) {
		super(g, id);
	}

	@Override
	public String whatIsThis() {
		return "civil information";
	}

	@Override
	protected String prettyName() {
		return firstName.get() + " " + name.get();
	}
}
