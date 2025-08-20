package byransha;

public class MimeTypeNode extends StringNode{

    public MimeTypeNode(BBGraph db, User creator, InstantiationInfo ii) {
        super(db, creator, ii);
        endOfConstructor();
    }

    @Override
    public void fromString(String s, User user) {
        String mimeType = "text/plain";
        if (s.startsWith("data:image/jpeg;base64,")) {mimeType = "image/jpeg";}
        else if (s.startsWith("data:image/gif;base64,")){mimeType = "image/gif";}
        else if (s.startsWith("data:image/svg+xml;base64,")){mimeType = "image/svg+xml";}
        else if (s.startsWith("data:application/pdf;base64,")) {mimeType = "application/pdf";}
        set(mimeType, user);
    }
}
