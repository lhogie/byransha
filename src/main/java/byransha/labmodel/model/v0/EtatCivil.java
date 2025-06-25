package byransha.labmodel.model.v0;

import byransha.*;

public class EtatCivil extends BusinessNode {
	public StringNode firstName;
	public StringNode name;
	public StringNode familyNameBeforeMariage;
	public DateNode birthDate;
	public StringNode cityOfBirth;
	public StringNode nationality;
	public DropdownNode<Country> countryOfBirth;
	public StringNode address;

	public Gender gender;

	public EtatCivil(BBGraph g) {
		super(g);
		firstName = BNode.create(g, StringNode.class);
		name = BNode.create(g, StringNode.class);
		familyNameBeforeMariage = BNode.create(g, StringNode.class);
		birthDate = BNode.create(g, DateNode.class);
		cityOfBirth = BNode.create(g, StringNode.class);
		nationality = BNode.create(g, StringNode.class);
		countryOfBirth = BNode.create(g, DropdownNode.class);
		address = BNode.create(g, StringNode.class);

		gender = BNode.create(g, Gender.class);

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
