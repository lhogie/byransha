package byransha;

import byransha.labmodel.model.v0.NodeBuilder;

public abstract class SystemNode extends BNode {
    public SystemNode(BBGraph g, InstantiationInfo ii) {
        super(g, g.systemUser(), ii);
        endOfConstructor();
    }
}
