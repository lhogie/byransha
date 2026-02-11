package byransha.nodes.lab.model.v0;

import byransha.BBGraph;
import byransha.nodes.primitive.StringNode;
import byransha.nodes.system.User;

public class Nationality extends StringNode {

    public Nationality(BBGraph db, User creator) {
        super(db,  creator);
    }

    @Override
    public String whatIsThis() {
        return "Nationality" + (get() != null ? " " + get() : "");
    }

    @Override
    public String prettyName() {
        return get() != null ? null + get() : null;
    }
}
