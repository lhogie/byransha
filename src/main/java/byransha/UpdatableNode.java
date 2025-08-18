package byransha;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lmu.Attribute;

public interface UpdatableNode {
    public void updateValue(String value, User user, BNode parent);
}
