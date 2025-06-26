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

	public RadioNode<String> gender;
    public ListCheckboxNode veryLongField;
    public BooleanNode human;

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

		gender = BNode.create(g, RadioNode.class);
        gender.addOptions("Homme", "Femme", "Autre");

        veryLongField = BNode.create(g, ListCheckboxNode.class);
        veryLongField.addOptions("Option 1", "Option 2", "Option 3", "Option 4",
                "Option 5", "Option 6", "Option 7", "Option 8", "Option 9", "Option 10",
                "Option 11", "Option 12", "Option 13", "Option 14", "Option 15");

        human = BNode.create(g, BooleanNode.class);

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
