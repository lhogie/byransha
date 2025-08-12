package byransha;

public abstract class SystemNode extends BNode {
public SystemNode(BBGraph g) {
    super(g, g.systemUser());
}
}
