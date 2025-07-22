package byransha.labmodel.model.gitMind.polerecherche;

import byransha.BBGraph;
import byransha.BNode;
import byransha.StringNode;
import byransha.labmodel.model.v0.BusinessNode;

public class Equipe extends BusinessNode {
    public StringNode nom;

    public Equipe(BBGraph g){
        super(g);
        nom = BNode.create(g, StringNode.class);
    }

    @Override
    public String whatIsThis() {
        if(nom != null) return nom.get();
        return "Equipe sans nom";
    }

    @Override
    public String prettyName() {
        return whatIsThis();
    }
}
