package byransha.labmodel.model.v0.gitMind.EnseignantChercheur;

import byransha.BBGraph;
import byransha.BNode;
import byransha.ListNode;
import byransha.StringNode;
import byransha.annotations.ListOptions;
import byransha.labmodel.model.v0.BusinessNode;

public class Chercheur extends EnseignantChercheur_Chercheur {
    @ListOptions(
            type = ListOptions.ListType.DROPDOWN,
            elementType = ListOptions.ElementType.STRING,
            source = ListOptions.OptionsSource.PROGRAMMATIC,
            allowCreation = false
    )
    public ListNode<StringNode> corpsGrade;


    public Chercheur(BBGraph g) {
        super(g);
        corpsGrade = BNode.create(this.graph, ListNode.class);
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
