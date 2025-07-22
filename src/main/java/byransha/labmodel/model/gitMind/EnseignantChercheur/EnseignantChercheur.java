package byransha.labmodel.model.gitMind.EnseignantChercheur;

import byransha.BBGraph;
import byransha.BNode;
import byransha.ListNode;
import byransha.StringNode;
import byransha.annotations.ListOptions;

public class EnseignantChercheur extends EnseignantChercheur_Chercheur {
    @ListOptions(
            type = ListOptions.ListType.DROPDOWN,
            elementType = ListOptions.ElementType.STRING,
            source = ListOptions.OptionsSource.PROGRAMMATIC,
            allowCreation = false
    )
    public ListNode<StringNode> corpsGrade;

    public StringNode AffectationEnseignement;
    public StringNode DoctoratEtat;
    public StringNode PEDR;

    public EnseignantChercheur(BBGraph g) {
        super(g);
        corpsGrade = BNode.create(this.graph, ListNode.class);

        AffectationEnseignement = BNode.create(g, StringNode.class);
        DoctoratEtat = BNode.create(g, StringNode.class);
        PEDR = BNode.create(g, StringNode.class);

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
