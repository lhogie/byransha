package byransha.labmodel.model.v0;

import byransha.*;
import byransha.annotations.ListSettings;
import byransha.annotations.Max;
import byransha.annotations.Pattern;
import byransha.annotations.Size;

public class EtatCivil extends BusinessNode {
    @Size(min = 2, max=5)
	public StringNode nomUsuel;
    public StringNode nomDeJeuneFille, prenom, villeDeNaissance, adressePersonnelle;
    public DropdownNode<Country> paysDeNaissance;
	public DateNode dateDeNaissance;
    @ListSettings(displayAsDropdown = true, allowCreation = false)
    @Size(max=2)
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
		adressePersonnelle = BNode.create(g, StringNode.class);
		telephone = BNode.create(g, PhoneNumberNode.class);

		this.color = "green";
	}

	public EtatCivil(BBGraph g, int id) {
		super(g, id);
	}

	@Override
	public String whatIsThis() {
		return "Etat Civil";
	}

	@Override
	public String prettyName() {
		return returnName();
	}

	private String returnName() {
		if( nomUsuel.get() != null && prenom.get() != null) {
			return nomUsuel.get() + " " + prenom.get();
		}
		else if( nomUsuel.get() != null && prenom.get() == null) {
			return nomUsuel.get() + " (pas de prénom)";
		}
		else if( prenom.get() != null && nomUsuel.get() == null) {
			return "(pas de nom) " + prenom.get();
		}
		return "Etat Civil sans information ";
	}
}
