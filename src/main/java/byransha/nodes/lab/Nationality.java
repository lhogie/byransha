package byransha.nodes.lab;

import byransha.graph.BGraph;
import byransha.nodes.primitive.StringNode;

public class Nationality extends StringNode {

    public Nationality(BGraph g){
        super(g);
    }

    @Override
    public String whatIsThis() {
        return "Nationality" + (get() != null ? " " + get() : "");
    }
}
