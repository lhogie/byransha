package byransha.nodes.system;

import byransha.BBGraph;
import byransha.nodes.BNode;

public abstract class SystemNode extends BNode {
    public SystemNode(BBGraph g) {
        super(g, g.systemUser());
    }
}
