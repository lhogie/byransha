package byransha;

public class PhoneNumberNode extends StringNode {

    public PhoneNumberNode(BBGraph db, User user ) {
        super(db, user);
    }

    @Override
    public String prettyName() {
        if( get() == null || get().isEmpty()) {
            return "Phone number (empty)";
        }

        return getAsString();
    }

    @Override
    public String whatIsThis() {
        return "PhoneNumberNode with value: " + get();
    }
}
