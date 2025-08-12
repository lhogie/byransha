package byransha;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Date;

public class ValueHistoryEntry<N> extends BNode {

    N value;
    DateNode date;
    User user;


    public ValueHistoryEntry(ValuedNode<N> vn, N value, OffsetDateTime date) {
        super(vn.graph, vn.graph.systemUser());
        this.value = value;
        this.date = new DateNode(graph, creator, date);
         this.user = creator;
         vn.saveValue(this, BBGraph.sysoutPrinter);
    }


    public ValueHistoryEntry(BBGraph g, User creator, int id) {
        super(g, g.systemUser(), id);
    }

    @Override
    public String whatIsThis() {
        return "a history entry";
    }

    @Override
    public String prettyName() {
        return value.toString();
    }

    @Override
    public boolean canEdit(User user) {
        return false;
    }
}
