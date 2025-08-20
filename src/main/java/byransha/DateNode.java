package byransha;

import java.io.IOException;
import java.time.OffsetDateTime;

public class DateNode extends PrimitiveValueNode<OffsetDateTime> {
    public DateNode(BBGraph g, User user, InstantiationInfo ii) {
        super(g, user, ii);
        endOfConstructor();
    }

    @Override
    protected void createOuts(User creator) {
        super.createOuts(creator);
    }

    public DateNode(BBGraph g, User user, InstantiationInfo ii, boolean historize) {
        super(g, user, ii, historize);
        endOfConstructor();
    }

    @Override
    protected OffsetDateTime bytesToValue(byte[] bytes, User user) throws IOException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try {
            return OffsetDateTime.parse(new String(bytes));
        } catch (Exception e) {
            throw new IOException("Failed to parse date from bytes", e);
        }
    }

    @Override
    protected byte[] valueToBytes(OffsetDateTime offsetDateTime) throws IOException {
        if (offsetDateTime == null) {
            return new byte[0];
        }
        try {
            return offsetDateTime.toString().getBytes();
        } catch (Exception e) {
            throw new IOException("Failed to convert date to bytes", e);
        }
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
            this.set(OffsetDateTime.parse(s), user);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format: " + s, e);
        }
    }
}
