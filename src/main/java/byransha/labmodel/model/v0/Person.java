package byransha.labmodel.model.v0;

import byransha.*;

public class Person extends BusinessNode {

    public EtatCivil etatCivil;

    @byransha.annotations.ListOptions(
        type = byransha.annotations.ListOptions.ListType.LIST
    )
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
            return "null";
        }
        return etatCivil.nomUsuel.get();
    }

    @Override
    public String prettyName() {
        return "Person: " + etatCivil.nomUsuel.get();
    }

    public Person(BBGraph g) {
        super(g);
        etatCivil = BNode.create(g, EtatCivil.class); // new EtatCivil(g);
        positions = BNode.create(g, ListNode.class); // new ListNode<>(g);
        pics = BNode.create(g, ImageNode.class); // new ImageNode(g);

        hdr = BNode.create(g, BooleanNode.class); // new BooleanNode(g);

        badgeNumber = BNode.create(g, StringNode.class); // new StringNode(g, null);
        website = BNode.create(g, StringNode.class); // new StringNode(g, null);
        faxNumber = BNode.create(g, StringNode.class); // new StringNode(g, null);
        phdDate = BNode.create(g, DateNode.class); // new StringNode(g, null);

        phoneNumbers = BNode.create(g, ListNode.class); // new ListNode<>(g);
        emailAddresses = BNode.create(g, ListNode.class); // new ListNode<>(g);
        offices = BNode.create(g, ListNode.class); // new ListNode<>(g);
    }

    public Person(BBGraph g, int id) {
        super(g, id);
    }

    @Override
    public String whatIsThis() {
        return "a physical person working in the lab";
    }
}
