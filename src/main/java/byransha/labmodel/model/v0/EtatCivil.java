package byransha.labmodel.model.v0;

import byransha.*;

public class EtatCivil extends BusinessNode {
	public StringNode nomUsuel, nomDeJeuneFille, prenom, villeDeNaissance, adressePersonnelle ;
	public DropdownNode<Country> paysDeNaissance;
	public DateNode dateDeNaissance;
	public SetNode<Country> nationalites;
	public PhoneNumberNode telephone;


	public EtatCivil(BBGraph g) {
		super(g);
		nomUsuel = BNode.create(g, StringNode.class);
		nomDeJeuneFille = BNode.create(g, StringNode.class);
		prenom = BNode.create(g, StringNode.class);
		dateDeNaissance = BNode.create(g, DateNode.class);
		villeDeNaissance = BNode.create(g, StringNode.class);
		paysDeNaissance = BNode.create(g, DropdownNode.class);
		nationalites = BNode.create(g, SetNode.class);
		nationalites.enableIsDropdown();
		adressePersonnelle = BNode.create(g, StringNode.class);
		telephone = BNode.create(g, PhoneNumberNode.class);

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
		if( nomUsuel.get() == null) {
			if( prenom.get() == null) {
				return "Civil Information";
			}
			return "(no usual name) " +  prenom.get();
		}
		return  nomUsuel.get() + " " + prenom.get();
	}
}
