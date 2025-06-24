package byransha.labmodel.model.v0;

import byransha.*;

public class EtatCivil extends BusinessNode {
	public StringNode firstName;
	public StringNode name;
	public StringNode familyNameBeforeMariage;
	public DateNode birthDate;
	public StringNode cityOfBirth;
	public DropdownNode<StringNode> nationality;
	public StringNode countryOfBirth;
	public StringNode address;

	public EtatCivil(BBGraph g) {
		super(g);
		firstName = BNode.create(g, StringNode.class); // new StringNode(g, null);
		name = BNode.create(g, StringNode.class); // new StringNode(g, null);
		familyNameBeforeMariage = BNode.create(g, StringNode.class); // new StringNode(g, null);
		birthDate = BNode.create(g, DateNode.class); // new StringNode(g, null);
		cityOfBirth = BNode.create(g, StringNode.class); // new StringNode(g, null);
		nationality = BNode.create(g, DropdownNode.class); // new StringNode(g, null);
		countryOfBirth = BNode.create(g, StringNode.class); // new StringNode(g, null);
		address = BNode.create(g, StringNode.class); // new StringNode(g, null);
		this.color = "green";
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
