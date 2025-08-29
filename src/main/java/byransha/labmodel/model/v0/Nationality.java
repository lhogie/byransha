package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.StringNode;
import byransha.User;
import byransha.ValuedNodeConstructionParameters;

public class Nationality extends StringNode {

    public Nationality(BBGraph db, User creator, InstantiationInfo ii) {
        super(db,  creator, ii);
        endOfConstructor();
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
