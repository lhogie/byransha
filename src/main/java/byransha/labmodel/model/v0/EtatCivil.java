package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.StringNode;

public class EtatCivil extends BusinessNode {
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
		firstName = g.addNode(StringNode.class);  // new StringNode(g, null);
		name = g.addNode(StringNode.class); //new StringNode(g, null);
		familyNameBeforeMariage = g.addNode(StringNode.class);  // new StringNode(g, null);
		birthDate = g.addNode(StringNode.class); //new StringNode(g, null);
		cityOfBirth = g.addNode(StringNode.class); //new StringNode(g, null);
		nationality = g.addNode(StringNode.class); //new StringNode(g, null);
		countryOfBirth = g.addNode(StringNode.class); //new StringNode(g, null);
		address = g.addNode(StringNode.class); //new StringNode(g, null);
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
