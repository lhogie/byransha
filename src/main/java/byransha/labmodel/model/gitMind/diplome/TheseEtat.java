package byransha.labmodel.model.gitMind.diplome;

import byransha.BBGraph;
import byransha.BNode;
import byransha.DateNode;
import byransha.StringNode;
import byransha.labmodel.model.v0.BusinessNode;

public class TheseEtat extends BusinessNode {
    public StringNode sujet;
    public DateNode dateTheseEtat;

    public TheseEtat(BBGraph g) {
        super(g);
        sujet = BNode.create(g, StringNode.class);
        dateTheseEtat = BNode.create(g, DateNode.class);
    }

    @Override
    public String whatIsThis() {
        return "État de la thèse";
    }

    @Override
    public String prettyName() {
        return "État de la thèse";
    }
}
