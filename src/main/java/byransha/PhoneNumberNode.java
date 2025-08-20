package byransha;

public class PhoneNumberNode extends StringNode {

    public PhoneNumberNode(BBGraph db, User user, InstantiationInfo ii) {
        super(db, user, ii);
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
