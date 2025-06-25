package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.BNode;
import byransha.BooleanNode;
import byransha.RadioNode;

import java.util.Set;

public class Gender extends RadioNode<String>{

    public Gender (BBGraph g){
        super(g);
        this.options.add("Homme");
        this.options.add("Femme");
        this.options.add("Autre");
    }

    public Gender(BBGraph g, int id) {
        super(g, id);
    }

    @Override
    public String whatIsThis() {
        return "Gender information";
    }

    @Override
    public String prettyName() {
        return "radio choice";
    }

}
