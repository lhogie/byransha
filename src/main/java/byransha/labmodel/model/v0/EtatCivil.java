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
		firstName = (StringNode) g.addNode(StringNode.class);  // new StringNode(g, null);
		name = (StringNode) g.addNode(StringNode.class); //new StringNode(g, null);
		familyNameBeforeMariage = (StringNode) g.addNode(StringNode.class);  // new StringNode(g, null);
		birthDate = (StringNode) g.addNode(StringNode.class); //new StringNode(g, null);
		cityOfBirth = (StringNode) g.addNode(StringNode.class); //new StringNode(g, null);
		nationality = (StringNode) g.addNode(StringNode.class); //new StringNode(g, null);
		countryOfBirth = (StringNode) g.addNode(StringNode.class); //new StringNode(g, null);
		address = (StringNode) g.addNode(StringNode.class); //new StringNode(g, null);
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
