package byransha;

import java.io.IOException;

public class ValueHistory<N> extends ListNode<ValueHistoryEntry<N>> {

    public ValueHistory(BBGraph g) {
        super(g, g.systemUser());

    }
    public ValueHistory(BBGraph g, int id, User user) {
        super(g, id, user);
    }

    @Override
    public String whatIsThis() {
        return "The history of values on a valued node";
    }

    @Override
    public String prettyName() {
        return size() + " history entries";
    }

    @Override
    public boolean canEdit(User user) {
        return false;
    }
}
