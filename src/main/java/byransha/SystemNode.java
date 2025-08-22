package byransha;

public abstract class SystemNode extends BNode {
    public SystemNode(BBGraph g, InstantiationInfo ii) {
        super(g, g.systemUser(), ii);
        endOfConstructor();
    }

    @Override
    protected void createOuts(User creator) {
        super.createOuts(creator);
    }
}
