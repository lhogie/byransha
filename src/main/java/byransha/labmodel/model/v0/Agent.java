package byransha.labmodel.model.v0;

import byransha.*;

public class Agent extends BusinessNode {

    public EtatCivil etatCivil;
    public SetNode<Position> positions;
    public IntNode badgeNumber;
    public ResearchGroup researchGroup;

    public SetNode<PhoneNumberNode> phoneNumbers;
    public SetNode<EmailNode> emailAddresses;
    public SetNode<Office> offices;

    public ImageNode pic;
    public ListNode<ImageNode> listPics;

    public Agent(BBGraph g) {
        super(g);

        etatCivil = BNode.create(g, EtatCivil.class);
        positions = BNode.create(g, SetNode.class);
        badgeNumber = BNode.create(g, IntNode.class);
        researchGroup = BNode.create(g, ResearchGroup.class);
        phoneNumbers = BNode.create(g, SetNode.class);
        emailAddresses = BNode.create(g, SetNode.class);
        offices = BNode.create(g, SetNode.class);

        pic = BNode.create(g, ImageNode.class);
        listPics = BNode.create(g, ListNode.class);
    }

    public Agent(BBGraph g, int id) {
        super(g, id);
    }

    private String returnName() {
        if (etatCivil == null || etatCivil.nomUsuel == null || etatCivil.nomUsuel.get() == null || etatCivil.nomUsuel.get().isEmpty()) {
            return "Agent: " + this.id() + " is an unnamed Agent";
        }
        return etatCivil.nomUsuel.get();
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
