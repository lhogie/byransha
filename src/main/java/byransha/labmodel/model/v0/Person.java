package byransha.labmodel.model.v0;

import byransha.*;
import byransha.annotations.ListOptions;

public class Person extends BusinessNode {

    public EtatCivil etatCivil;

    @ListOptions(type = ListOptions.ListType.LIST)
    public ListNode<Position> positions;

    public ImageNode pics;
    public BooleanNode hdr;

    public StringNode badgeNumber;
    public StringNode website;
    public StringNode faxNumber;
    public ResearchGroup researchGroup;
    public DateNode phdDate;

    public ListNode<StringNode> phoneNumbers;

    protected ListNode<EmailNode> emailAddresses;

    public ListNode<Office> offices;

    protected ListNode<ACMClassifier> topics;

    public StringNode quotite;

    public Position position;
    public boolean enposte;
    public StringNode researchActivity;

    @Override
    public String toString() {
        if (etatCivil == null) {
            return super.toString();
        }
        return etatCivil.nomUsuel.get();
    }

    @Override
    public String prettyName() {
        return "Person: " + etatCivil.nomUsuel.get();
    }

    public Person(BBGraph g) {
        super(g);
        etatCivil = g.create( EtatCivil.class); // new EtatCivil(g);
        positions = g.create( ListNode.class); // new ListNode<>(g);
        pics = g.create( ImageNode.class); // new ImageNode(g);

        hdr = g.create( BooleanNode.class); // new BooleanNode(g);

        badgeNumber = g.create( StringNode.class); // new StringNode(g, null);
        website = g.create( StringNode.class); // new StringNode(g, null);
        faxNumber = g.create( StringNode.class); // new StringNode(g, null);
        phdDate = g.create( DateNode.class); // new StringNode(g, null);

        phoneNumbers = g.create( ListNode.class); // new ListNode<>(g);
        emailAddresses = g.create( ListNode.class); // new ListNode<>(g);
        offices = g.create( ListNode.class); // new ListNode<>(g);
    }

    public Person(BBGraph g, int id) {
        super(g, id);
    }

    @Override
    public String whatIsThis() {
        return "a physical person working in the lab";
    }
}
