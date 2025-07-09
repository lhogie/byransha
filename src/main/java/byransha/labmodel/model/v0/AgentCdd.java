package byransha.labmodel.model.v0;

import byransha.*;

public class AgentCdd extends Agent {

    public StringNode login, siteRecherche, siAutreSiteDeRecherche, bureau, siteWeb, doctorat, etablisementDelivranceDoctorat, ecoleDoctoraleSiEnFrance, diplomeDIngenieur, delivrePar, nomDuDernierOrganismeFrequente;
    public IntNode badge;
    public DateNode dateDoctorat, dateDelivrance;
    public EmailNode email;
    public PhoneNumberNode telephoneProfessionnel, telephone2;
    public Position positionDansCetOrganisme;

    public StringNode nomDeLEncadrant, pole, equipe, siAutrePole, sourceDeFinancment, siAutreSourceDeFinancement, organismeGestionnaire, siAutreOrganismeGestionnaire, informationsComplementaires, devenir ;
    public DateNode dateDArrivee, dateDeDepart;



    public AgentCdd(BBGraph g) {
        super(g);
        login = BNode.create(g, StringNode.class);
        siteRecherche = BNode.create(g, StringNode.class);
        siAutreSiteDeRecherche = BNode.create(g, StringNode.class);
        bureau = BNode.create(g, StringNode.class);
        siteWeb = BNode.create(g, StringNode.class);
        doctorat = BNode.create(g, StringNode.class);
        etablisementDelivranceDoctorat = BNode.create(g, StringNode.class);
        ecoleDoctoraleSiEnFrance = BNode.create(g, StringNode.class);
        diplomeDIngenieur = BNode.create(g, StringNode.class);
        delivrePar = BNode.create(g, StringNode.class);
        nomDuDernierOrganismeFrequente = BNode.create(g, StringNode.class);
        badge = BNode.create(g, IntNode.class);
        dateDoctorat = BNode.create(g, DateNode.class);
        dateDelivrance = BNode.create(g, DateNode.class);
        email = BNode.create(g, EmailNode.class);
        telephoneProfessionnel = BNode.create(g, PhoneNumberNode.class);
        telephone2 = BNode.create(g, PhoneNumberNode.class);
        positionDansCetOrganisme = BNode.create(g, Position.class);

        nomDeLEncadrant = BNode.create(g, StringNode.class);
        pole = BNode.create(g, StringNode.class);
        equipe = BNode.create(g, StringNode.class);
        siAutrePole = BNode.create(g, StringNode.class);
        sourceDeFinancment = BNode.create(g, StringNode.class);
        siAutreSourceDeFinancement = BNode.create(g, StringNode.class);
        organismeGestionnaire = BNode.create(g, StringNode.class);
        siAutreOrganismeGestionnaire = BNode.create(g, StringNode.class);
        informationsComplementaires = BNode.create(g, StringNode.class);
        devenir = BNode.create(g, StringNode.class);
        dateDArrivee = BNode.create(g, DateNode.class);
        dateDeDepart = BNode.create(g, DateNode.class);
        this.setColor("#fcba03");
    }

    public AgentCdd(BBGraph g, int id) {
        super(g, id);
    }

    @Override
    public String whatIsThis() {
        return returnName();
    }

    @Override
    public String prettyName() {
        return returnName();
    }

    public String returnName() {
        if(etatCivil.nomUsuel.get() != null && etatCivil.prenom.get() != null) {;
            return etatCivil.nomUsuel.get() + " " + etatCivil.prenom.get();
        } else if(etatCivil.nomUsuel.get() != null && etatCivil.prenom.get() == null) {
            return etatCivil.nomUsuel.get() + " (pas de prenom)";
        } else if(etatCivil.prenom.get() != null && etatCivil.nomUsuel.get() == null) {
            return "(pas de nom) " + etatCivil.prenom.get();
        } else {
            return "Agent CDD sans information";
        }
    }
}
