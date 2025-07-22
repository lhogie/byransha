package byransha.labmodel.model.gitMind.EnseignantChercheur;

import byransha.BBGraph;
import byransha.BNode;
import byransha.DateNode;
import byransha.StringNode;
import byransha.labmodel.model.v0.BusinessNode;
import byransha.labmodel.model.gitMind.diplome.Doctorat;
import byransha.labmodel.model.gitMind.diplome.TheseEtat;

public class EnseignantChercheur_Chercheur extends BusinessNode {
    public TheseEtat informationTheseEtat;
    public Doctorat informationDoctorat;
    public DateNode dateArrivee;

    //TODO Create right type of nodes
    public StringNode TypeAppartenanceAI3S;
    public StringNode SectionCNRS;
    public StringNode SectionCNU;
    public StringNode SiteRecherche;

    public EnseignantChercheur_Chercheur(BBGraph g) {
        super(g);
        informationTheseEtat = BNode.create(g,TheseEtat.class);
        informationDoctorat = BNode.create(g,Doctorat.class);

        dateArrivee = BNode.create(g, DateNode.class);
        TypeAppartenanceAI3S = BNode.create(g, StringNode.class);
        SectionCNRS = BNode.create(g, StringNode.class);
        SectionCNU = BNode.create(g, StringNode.class);
        SiteRecherche = BNode.create(g, StringNode.class);

    }

    @Override
    public String whatIsThis() {
        return "";
    }

    @Override
    public String prettyName() {
        return "";
    }
}
