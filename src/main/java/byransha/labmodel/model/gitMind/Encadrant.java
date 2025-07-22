package byransha.labmodel.model.gitMind;

import byransha.*;
import byransha.labmodel.model.v0.BusinessNode;

public class Encadrant extends BusinessNode {

    public StringNode nom, prenom;
    public EmailNode emailEncadrant;
    public PhoneNumberNode telephoneEncadrant;

    public Encadrant(BBGraph g) {
        super(g);
        nom = BNode.create(g, StringNode.class);
        prenom = BNode.create(g, StringNode.class);
        emailEncadrant = BNode.create(g, EmailNode.class);
        telephoneEncadrant = BNode.create(g, PhoneNumberNode.class);
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
