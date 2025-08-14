package byransha;

import java.io.IOException;
import java.time.OffsetDateTime;

public class ValueHistoryEntry<N> extends BNode {
    N value;
    DateNode date;
    User user;


    public ValueHistoryEntry(ValuedNode<N> vn, N value, OffsetDateTime date) throws IOException {
        super(vn.graph, vn.graph.systemUser());
        this.value = value;
        this.date = new DateNode(graph, creator, date);
        this.user = creator;
        vn.saveValue(this, BBGraph.sysoutPrinter);
        endOfConstructor();
    }


    public ValueHistoryEntry(BBGraph g, User creator, int id) {
        super(g, g.systemUser(), id);
        endOfConstructor();
    }

    @Override
    public String whatIsThis() {
        return "an element of history for a given node";
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
