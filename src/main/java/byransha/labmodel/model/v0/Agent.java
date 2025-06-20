package byransha.labmodel.model.v0;

import byransha.*;

public class Agent extends BusinessNode {

    public EtatCivil etatCivil;
    public ListNode<Position> positions;
    public IntNode badgeNumber;
    public ResearchGroup researchGroup;

    public ListNode<PhoneNumberNode> phoneNumbers;
    public ListNode<EmailNode> emailAddresses;
    public ListNode<Office> offices;

    public ImageNode pic;
    public ListNode<ImageNode> listPics;

    public Agent(BBGraph g) {
        super(g);

        etatCivil = BNode.create(g, EtatCivil.class);
        positions = BNode.create(g, ListNode.class);
        badgeNumber = BNode.create(g, IntNode.class);
        researchGroup = BNode.create(g, ResearchGroup.class);
        phoneNumbers = BNode.create(g, ListNode.class);
        emailAddresses = BNode.create(g, ListNode.class);
        offices = BNode.create(g, ListNode.class);

        pic = BNode.create(g, ImageNode.class);
        listPics = BNode.create(g, ListNode.class);
    }

    public Agent(BBGraph g, int id) {
        super(g, id);
    }

    private String returnName() {
        if (etatCivil == null || etatCivil.name == null || etatCivil.name.get() == null || etatCivil.name.get().isEmpty()) {
            return "Agent: " + this.id() + " is an unnamed Agent";
        }
        return etatCivil.name.get();
    }

    @Override
    public String toString() {
        return returnName();
    }

    @Override
    public String prettyName() {
        return returnName();
    }

    @Override
    public String whatIsThis() {
        return returnName();
    }
}
