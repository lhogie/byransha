package byransha;

public abstract class SystemNode extends BNode {
    public SystemNode(BBGraph g, InstantiationInfo ii) {
        super(g, g.systemUser(), ii);
        endOfConstructor();
    }
}
