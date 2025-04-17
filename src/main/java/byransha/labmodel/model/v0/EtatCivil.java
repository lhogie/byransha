package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.BNode;
import byransha.StringNode;

public class EtatCivil extends BNode {
	public StringNode firstName;
	public StringNode name;
	public StringNode familyNameBeforeMariage;
	public StringNode birthDate;
	public StringNode cityOfBirth;
	public StringNode nationality;
	public StringNode countryOfBirth;
	public StringNode address;

	public EtatCivil(BBGraph g) {
		super(g);
		firstName = new StringNode(g, null);
		name = new StringNode(g, null);
		familyNameBeforeMariage = new StringNode(g, null);
		birthDate = new StringNode(g, null);
		cityOfBirth = new StringNode(g, null);
		nationality = new StringNode(g, null);
		countryOfBirth = new StringNode(g, null);
		address = new StringNode(g, null);
	}

	public EtatCivil(BBGraph g, int id) {
		super(g, id);
	}

	@Override
	public String whatIsThis() {
		return "civil information";
	}

	@Override
	public String prettyName() {
		return firstName.get() + " " + name.get();
	}
}
