package byransha.labmodel.model.v0.gitMind.diplome;

import byransha.BBGraph;
import byransha.BNode;
import byransha.DateNode;
import byransha.StringNode;
import byransha.labmodel.model.v0.BusinessNode;

public class Doctorat extends BusinessNode {

    public DateNode dateObtention;
    public StringNode etablissement;
    public StringNode ecoleDoctoraleSiFrance;


    public Doctorat(BBGraph g) {
        super(g);
        dateObtention = BNode.create(g, DateNode.class);
        etablissement = BNode.create(g, StringNode.class);
        ecoleDoctoraleSiFrance = BNode.create(g, StringNode.class);
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
