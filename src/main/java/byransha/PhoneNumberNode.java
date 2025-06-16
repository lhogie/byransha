package byransha;

public class PhoneNumberNode extends IntNode {

    public PhoneNumberNode(BBGraph db) {
        super(db);
    }

    @Override
    public String prettyName() {
        return "Phone number";
    }

    @Override
    public String whatIsThis() {
        return "PhoneNumberNode with value: " + get();
    }
}
