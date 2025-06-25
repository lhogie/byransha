package byransha;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.node.JsonNodeType;

import java.io.IOException;
import java.util.Set;

public abstract class RadioNode<N> extends ValuedNode<Integer> {

    public Set<N> options = Set.of();

    public RadioNode(BBGraph db) {
        super(db);
        set(0); // Default value
    }

    public RadioNode(BBGraph db, int id) {
        super(db, id);
        set(0); // Default value
    }



    @Override
    public void fromString(String s) {
        try {
            int value = Integer.parseInt(s);
            set(value);
        } catch (NumberFormatException e) {
            System.err.println("Invalid value for RadioNode: " + s);
            throw new IllegalArgumentException("Invalid value for RadioNode: " + s, e);
        }
    }

    @Override
    public String whatIsThis() {
        return "";
    }

    @Override
    public String prettyName() {
        return "";
    }
}
