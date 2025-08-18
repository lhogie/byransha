package byransha;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.function.Consumer;

public abstract class PrimitiveValueNode<V>  extends ValuedNode<V>  {


    public PrimitiveValueNode(BBGraph db, User user) {
        super(db, user, true);
        endOfConstructor();
    }

    public PrimitiveValueNode(BBGraph db, User user, int id) {
        super(db, user, id);
        endOfConstructor();
    }

    public abstract void fromString(String s, User user);
}
