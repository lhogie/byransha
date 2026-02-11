package byransha.nodes.primitive;

import java.io.IOException;

import byransha.nodes.BNode;
import byransha.BBGraph;
import byransha.nodes.system.User;
import toools.text.TextUtilities;

public abstract class ValuedNode<V> extends BNode {
    V value;

    public ValuedNode(BBGraph g, User user) {
        super(g, user);
    }


    protected abstract byte[] valueToBytes(V v) throws IOException;

    protected abstract V bytesToValue(byte[] bytes, User user) throws IOException;

    @Override
    public final String toString() {
        return getClass().getSimpleName() + ": " + value == null ? "no value" : value.toString();
    }

    public String getAsString() {
        return value != null ? value.toString() : "";
    }

    @Override
    public int distanceToSearchString(String searchString, User user) {
        return TextUtilities.computeLevenshteinDistance(
            searchString,
            get(user).toString()
        );
    }

    public V get(User user) {
        if (!canSee(user))
            throw new RuntimeException(user + " is not allowed to read the value");

        return value;
    }

    public void set(V v, User user) {
        if (!canEdit(user))
            throw new RuntimeException(user + " is not allowed to set value");

            value = v;
    }

    public abstract Object defaultValue();
}
