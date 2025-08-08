package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.StringNode;

public class Nationality extends StringNode {

    public Nationality(BBGraph db) {
        super(db);
    }

    public Nationality(BBGraph db, int id) {
        super(db, id);
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
