package byransha.nodes.primitive;

import byransha.BBGraph;
import byransha.nodes.system.User;

import java.io.IOException;
import java.time.OffsetDateTime;

public class DateNode extends PrimitiveValueNode<OffsetDateTime> {
    public DateNode(BBGraph g, User user) {
        super(g, user);
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
