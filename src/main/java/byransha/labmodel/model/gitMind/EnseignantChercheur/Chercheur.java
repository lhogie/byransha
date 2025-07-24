package byransha.labmodel.model.gitMind.EnseignantChercheur;

import byransha.*;
import byransha.annotations.ListOptions;

public class Chercheur extends EnseignantChercheur_Chercheur {
    @ListOptions(
            type = ListOptions.ListType.DROPDOWN,
            elementType = ListOptions.ElementType.STRING,
            source = ListOptions.OptionsSource.PROGRAMMATIC,
            allowCreation = false
    )
    public ListNode<StringNode> corpsGrade;

    public DateNode dateArrive;
    public BooleanNode parti;
    public DateNode dateDeparture;


    public Chercheur(BBGraph g) {
        super(g);
        corpsGrade = BNode.create(this.graph, ListNode.class);
        dateArrive = BNode.create(this.graph, DateNode.class);
        parti = BNode.create(this.graph, BooleanNode.class);
        dateDeparture = BNode.create(this.graph, DateNode.class);
        parti.setNodeToSetVisible(dateDeparture);

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
