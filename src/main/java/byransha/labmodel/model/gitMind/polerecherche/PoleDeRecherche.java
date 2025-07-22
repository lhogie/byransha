package byransha.labmodel.model.gitMind.polerecherche;

import byransha.BBGraph;
import byransha.BNode;
import byransha.ListNode;
import byransha.StringNode;
import byransha.annotations.ListOptions;
import byransha.labmodel.model.v0.BusinessNode;

public class PoleDeRecherche extends BusinessNode {
    public StringNode nom;
    @ListOptions(
            type = ListOptions.ListType.DROPDOWN
    )
    public ListNode<Equipe> equipes;

    public PoleDeRecherche(BBGraph g){
        super(g);
        nom = BNode.create(g, StringNode.class);
        equipes = BNode.create(g, ListNode.class);
    }

    @Override
    public String whatIsThis() {
        if(nom != null) return nom.get();
        return "PoleDeRecherche sans nom";
    }

    @Override
    public String prettyName() {
        return whatIsThis();
    }
}
