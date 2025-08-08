package byransha;

public class StringNode extends ValuedNode<String> {

    public StringNode(BBGraph db) {
        super(db);
    }

    public StringNode(BBGraph g, String init) {
        super(g);
        set(init);
    }

    public StringNode(BBGraph db, int id) {
        super(db, id);
    }

    @Override
    public String prettyName() {
        return "string";
    }

    @Override
    public void fromString(String s) {
        set(s);
    }


    @Override
    public String whatIsThis() {
        return "a sequence of characters";
    }
}
