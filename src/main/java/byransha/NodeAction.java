package byransha;

import byransha.nodes.BNode;
import byransha.nodes.system.User;

public abstract class NodeAction {
    public abstract String description();
    public abstract BNode exec(User user);
}
