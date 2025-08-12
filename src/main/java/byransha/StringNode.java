package byransha;

public class StringNode extends PrimitiveValueNode<String> {

    public StringNode(BBGraph db, User creator) {
        super(db, creator);
    }

    public StringNode(BBGraph g, User creator, String init) {
        super(g, creator);
        set(init, creator);
    }

    public StringNode(BBGraph db, int id) {
        super(db, id);
    }

    @Override
    public String prettyName() {
        return get();
    }

    @Override
    public void fromString(String s, User creator) {
        set(s, creator);
    }


    @Override
    public String whatIsThis() {
        return "a sequence of characters";
    }
}
