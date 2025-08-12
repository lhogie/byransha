package byransha;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public class DateNode extends PrimitiveValueNode<OffsetDateTime> {
    private OffsetDateTime date;

    public DateNode(BBGraph g, User user, OffsetDateTime v) {
        super(g, user);
        this.date = v;
    }

    public DateNode(BBGraph g) {
        super(g);
    }

    public DateNode(BBGraph g, User user, int id) {
        super(g, user, id);
    }
}
