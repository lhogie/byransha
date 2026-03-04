package byransha.nodes.lab;

import byransha.graph.BGraph;
import byransha.nodes.primitive.StringNode;
import byransha.nodes.system.User;

public class Nationality extends StringNode {

    public Nationality(BGraph g){
        super(g);
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
