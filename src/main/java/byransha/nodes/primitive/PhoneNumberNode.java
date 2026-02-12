package byransha.nodes.primitive;

import byransha.graph.BBGraph;
import byransha.nodes.system.User;

public class PhoneNumberNode extends StringNode {

    public PhoneNumberNode(BBGraph db, User user) {
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
