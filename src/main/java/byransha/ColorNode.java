package byransha;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ColorNode extends PrimitiveValueNode<String> {

    public ColorNode(BBGraph g, User creator, InstantiationInfo ii) {
        super(g, creator, ii);
        endOfConstructor();
    }

    @Override
    protected void createOuts(User creator) {
        super.createOuts(creator);
    }

    @Override
    public void fromString(String s, User creator) {
        set(s, creator);
    }

    @Override
    protected byte[] valueToBytes(String s) throws IOException {
        if (s == null) {
            return new byte[0];
        }
        String cleanHex = s.replace("#", "").trim();
        if (cleanHex.length() != 6) {
            throw new IOException("Color hex string must be 6 characters long");
        }
        byte[] bytes = new byte[3];
        try {
            bytes[0] = (byte) Integer.parseInt(cleanHex.substring(0, 2), 16);
            bytes[1] = (byte) Integer.parseInt(cleanHex.substring(2, 4), 16);
            bytes[2] = (byte) Integer.parseInt(cleanHex.substring(4, 6), 16);
            return bytes;
        } catch (NumberFormatException e) {
            throw new IOException("Invalid hex color format");
        }
    }

    @Override
    protected String bytesToValue(byte[] bytes, User user) throws IOException {
        if (bytes == null || bytes.length != 3) {
            throw new IOException("Color bytes must be exactly 3 bytes long");
        }
        String hex = String.format("#%02X%02X%02X", bytes[0] & 0xFF, bytes[1] & 0xFF, bytes[2] & 0xFF);
        set(hex, user);
        return hex;
    }

    @Override
    public String whatIsThis() {
        return "a color";
    }

    @Override
    public String prettyName() {
        return get() != null ? get() : "null";
    }

}
