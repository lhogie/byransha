package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.StringNode;
import byransha.User;

public class Nationality extends StringNode {

    public Nationality(BBGraph db, User creator) {
        super(db,  creator);
    }

    public Nationality(BBGraph db,  User creator, int id) {
        super(db, creator, id);
    }

    @Override
    public String whatIsThis() {
        return "Nationality" + (get() != null ? " " + get() : "");
    }

    @Override
    public String prettyName() {
        return get() != null ? "" + get() : "Nationality (unknown)";
    }
}
