package byransha.labmodel.model.v0;

import byransha.*;
import byransha.annotations.*;
import byransha.annotations.ListOptions;

public class EtatCivil extends BusinessNode {

    @Size(min = 2)
    public StringNode nomUsuel;

    @Required
    public StringNode nomDeJeuneFille, prenom, villeDeNaissance, adressePersonnelle;

    @ListOptions(type = ListOptions.ListType.DROPDOWN, allowCreation = false)
    public ListNode<Country> paysDeNaissance;

    public DateNode dateDeNaissance;

    @ListOptions(
        type = ListOptions.ListType.MULTIDROPDOWN,
        allowCreation = false,
        allowMultiple = true
    )
    @Size(max = 2)
    public ListNode<Country> nationalites;

    @Pattern(
        regex = "^[\\+]?[(]?[0-9]{3}[)]?[-\\s\\.]?[0-9]{3}[-\\s\\.]?[0-9]{4,6}$"
    )
    public PhoneNumberNode telephone;

    public ImageNode pic;

    public EtatCivil(BBGraph g) {
        super(g);
        nomUsuel = g.create( StringNode.class);
        nomDeJeuneFille = g.create( StringNode.class);
        prenom = g.create( StringNode.class);
        dateDeNaissance = g.create( DateNode.class);
        villeDeNaissance = g.create( StringNode.class);
        paysDeNaissance = g.create( ListNode.class);
        nationalites = g.create( ListNode.class);
        adressePersonnelle = g.create( StringNode.class);
        telephone = g.create( PhoneNumberNode.class);
        pic = g.create( ImageNode.class);
        this.setColor("#03fc62");
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
        if (nomUsuel.get() != null && prenom.get() != null) {
            return nomUsuel.get() + " " + prenom.get();
        } else if (nomUsuel.get() != null && prenom.get() == null) {
            return nomUsuel.get() + " (pas de prénom)";
        } else if (prenom.get() != null && nomUsuel.get() == null) {
            return "(pas de nom) " + prenom.get();
        }
        return "Etat Civil sans information ";
    }
}
