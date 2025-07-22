package byransha.labmodel.model.gitMind.gestionnaire;

import byransha.*;

import java.util.ArrayList;
import java.util.List;

public class Gestionnaire extends User {

    public ListNode<StringNode> listDesFiltres;

    public Gestionnaire(BBGraph g) {
        super(g);
        setGestionnaire();
        listDesFiltres = BNode.create(g, ListNode.class);
    }

    @Override
    public String whatIsThis() {
        if(this.name != null) return this.name.getAsString();
        return "Gestionnaire sans nom";
    }

    @Override
    public String prettyName(){
        return whatIsThis();
    }

    public List<String> getFiltres() {
        List<String> filter = new ArrayList<>();
        if(listDesFiltres != null){
            listDesFiltres.forEachOut((n, out) -> {
                if(out instanceof StringNode o) filter.add(o.get());
            });
            return filter;
        }
        return  filter;
    }
}
