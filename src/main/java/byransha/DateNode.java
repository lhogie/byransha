package byransha;

import java.time.OffsetDateTime;

public class DateNode extends PrimitiveValueNode<OffsetDateTime> {
    private OffsetDateTime date;

    public DateNode(BBGraph g, User user, OffsetDateTime v) {
        super(g, user);
        this.date = v;
    }

    public DateNode(BBGraph g, User user) {
        super(g, user);
    }

    public DateNode(BBGraph g, User user, int id) {
        super(g, user, id);
    }

    @Override
    public String whatIsThis() {
        return "DateNode";
    }

    @Override
    public String prettyName() {
        return "Date";
    }

    @Override
    public void fromString(String s, User user) {
        try {
            this.date = OffsetDateTime.parse(s);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format: " + s, e);
        }
    }
}
