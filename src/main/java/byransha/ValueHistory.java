package byransha;

import java.io.IOException;
import java.time.OffsetDateTime;

public class ValueHistory<N> extends ListNode<ValueHistoryEntry<N>> implements ValueHolder<N> {

    private  ValuedNode<N> valuedNode;

    public ValueHistory(ValuedNode<N> vn) {
        super(vn.graph, vn.graph.systemUser(), false);
        this.valuedNode = vn;
        endOfConstructor();
    }

    public ValueHistory(BBGraph g, User user, int id) {
        super(g,  user, id);
        endOfConstructor();
    }

    @Override
    public String whatIsThis() {
        return "value history for a specific (valued) node";
    }

    @Override
    public String prettyName() {
        return size() + " history entries";
    }

    @Override
    public boolean canEdit(User user) {
        return false;
    }

    @Override
    public N getValue(){
        if (size() == 0){
            return null;
        }
        else{
            return getElements().getLast().value;
//            return history.getAt(graph.date());
        }
    }

    @Override
    public void setValue(N value, User user){
        try{
            var e = new ValueHistoryEntry<N>(valuedNode, value, OffsetDateTime.now());
            add(e, user);
        }
        catch (IOException err){
            throw new IllegalStateException(err);
        }
    }


}
