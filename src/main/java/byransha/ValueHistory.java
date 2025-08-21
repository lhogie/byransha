package byransha;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;

public class ValueHistory<N> extends ListNode<ValueHistoryEntry<N>> implements ValueHolder<N> {

    private ValuedNode<N> valuedNode;

    public ValueHistory(BBGraph g, User creator, InstantiationInfo ii) {
        super(g, creator, ii, false);
        endOfConstructor();
    }

    public ValueHistory(ValuedNode<N> vn) {
        super(vn.g, vn.g.systemUser(), InstantiationInfo.persisting, false);
        this.valuedNode = vn;
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
            return getElements().getLast().value();
            //return history.getAt(graph.date());
        }
    }

    @Override
    public void setValue(N value, User user){
        try{
            var e = new ValueHistoryEntry<N>(valuedNode, value, OffsetDateTime.now(), user, InstantiationInfo.persisting);
            add(e, user);
            save();
        }
        catch (IOException err){
            throw new IllegalStateException(err);
        }
    }

    @Override
    public void save() throws IOException {
        super.save();
    }
}
