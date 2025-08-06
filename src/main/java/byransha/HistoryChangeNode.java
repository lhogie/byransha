package byransha;

public class HistoryChangeNode extends StringNode {

    public HistoryChangeNode(BBGraph g) {
        super(g);
    }

    public HistoryChangeNode(BBGraph g, int id) {
        super(g, id);
    }

    @Override
    public void fromString(String s) {
        set(s);
        set(s);
    }

    @Override
    public String whatIsThis() {
        return "The history of changes made to a node represented as a string.";
    }

    @Override
    public String prettyName() {
        return "History Change Node";
    }

    @Override
    public void set(String newValue) {
        System.out.println(this.value);
        if (this.value == null) this.value = newValue;
        else this.value += "\n" + newValue;
        if (directory() != null) {
            saveValue(BBGraph.sysoutPrinter);
        }
    }

    @Override
    public boolean canEdit(User user) {
        return false;
    }
}
