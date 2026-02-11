package byransha.nodes.primitive;

import byransha.BBGraph;
import byransha.nodes.system.User;

public abstract class PrimitiveValueNode<V>  extends ValuedNode<V>  {

    boolean undefined = false;

    public PrimitiveValueNode(BBGraph g, User user) {
        super(g, user);
    }

    public abstract void fromString(String s, User user);
}
