package byransha;

public class PhoneNumberNode extends StringNode {

    public PhoneNumberNode(BBGraph db, User user ) {
        super(db, user);
        endOfConstructor();
    }

    public PhoneNumberNode(BBGraph db, User user, int id ) {
        super(db, user, id);
        endOfConstructor();
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
