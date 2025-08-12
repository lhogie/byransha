package byransha;

public class ColorNode extends PrimitiveValueNode<String> {

    public ColorNode(BBGraph g, User creator) {
        super(g, creator);
        this.setMimeType("text/hex");
    }

    public ColorNode(BBGraph g, User creator, int id) {
        super(g, creator, id);
    }

    @Override
    public void fromString(String s, User creator) {
        set(s, creator);
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
