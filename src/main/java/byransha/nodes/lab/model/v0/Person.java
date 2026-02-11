package byransha.nodes.lab.model.v0;

import byransha.BBGraph;
import byransha.annotations.ListOptions;
import byransha.nodes.DocumentNode;
import byransha.nodes.system.User;
import byransha.nodes.primitive.*;

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

    @ListOptions(type = ListOptions.ListType.LIST)
    public ListNode<StringNode> phoneNumbers;

    @ListOptions(type = ListOptions.ListType.LIST)
    protected ListNode<EmailNode> emailAddresses;

    @ListOptions(type = ListOptions.ListType.LIST)
    public ListNode<Office> offices;

    @ListOptions(type = ListOptions.ListType.LIST)
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
        if (etatCivil == null) {
            return null;
        }

        return etatCivil.prettyName();
    }

    public Person(BBGraph g, User creator) {
        super(g, creator);
        etatCivil = new EtatCivil(g, creator);
        positions = new ListNode(g, creator);
        pics = new DocumentNode(g, creator);
        hdr = new BooleanNode(g, creator, false);
        badgeNumber = new StringNode(g, creator);
        website = new StringNode(g, creator);
        faxNumber = new StringNode(g, creator);
        phdDate = new DateNode(g, creator);
        phoneNumbers = new ListNode(g, creator);
        emailAddresses = new ListNode(g, creator);
        offices = new ListNode(g, creator);
    }


    @Override
    public String whatIsThis() {
        return "a physical person working in the lab";
    }
}
