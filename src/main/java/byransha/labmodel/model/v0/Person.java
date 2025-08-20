package byransha.labmodel.model.v0;

import byransha.*;
import byransha.annotations.ListOptions;

public class Person extends BusinessNode {

    public EtatCivil etatCivil;

    @ListOptions(type = ListOptions.ListType.LIST)
    public ListNode<Position> positions;

    public DocumentNode pics;
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
        return etatCivil.name.get();
    }

    @Override
    public String prettyName() {
        return "Person: " + etatCivil.name.get();
    }

    public Person(BBGraph g, User creator, InstantiationInfo ii) {
        super(g, creator, ii);
        endOfConstructor();
    }

    @Override
    protected void createOuts(User creator) {
        etatCivil = new EtatCivil(g, creator, InstantiationInfo.persisting); // new EtatCivil(g);
        positions = new ListNode(g, creator, InstantiationInfo.persisting); // new ListNode<>(g);
        pics = new DocumentNode(g, creator, InstantiationInfo.persisting); // new ImageNode(g);

        hdr = new BooleanNode(g, creator, InstantiationInfo.persisting); // new BooleanNode(g);

        badgeNumber = new StringNode(g, creator, InstantiationInfo.persisting); // new StringNode(g, null);
        website = new StringNode(g, creator, InstantiationInfo.persisting); // new StringNode(g, null);
        faxNumber = new StringNode(g, creator, InstantiationInfo.persisting); // new StringNode(g, null);
        phdDate = new DateNode(g, creator, InstantiationInfo.persisting); // new StringNode(g, null);

        phoneNumbers = new ListNode(g, creator, InstantiationInfo.persisting); // new ListNode<>(g);
        emailAddresses = new ListNode(g, creator, InstantiationInfo.persisting); // new ListNode<>(g);
        offices = new ListNode(g, creator, InstantiationInfo.persisting); // new ListNode<>(g);
    }

    @Override
    public String whatIsThis() {
        return "a physical person working in the lab";
    }
}
