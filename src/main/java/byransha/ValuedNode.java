package byransha;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.function.Consumer;

import toools.text.TextUtilities;

public abstract class ValuedNode<V> extends BNode  {
    ValueHolder<V> valueHolder;
    private final boolean historize;

    protected ValuedNode(BBGraph g, User user, InstantiationInfo ii, boolean historize) {
        super(g, user, ii);
        this.historize = historize;
        endOfConstructor();
    }

    @Override
    protected void createOuts(User creator) {
        this.valueHolder = historize ? new ValueHistory<V>(this) : new SimpleValueHolder<>();
    }

    protected abstract byte[] valueToBytes(V v) throws IOException;

    protected abstract V bytesToValue(byte[] bytes, User user) throws IOException;

    @Override
    public final String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append("(value=\"").append(get()).append("\")");
        return sb.toString();
    }

    public String getAsString() {
        V v = get();
        return v != null ? valueHolder.toString() : "";
    }

    @Override
    public int distanceToSearchString(String searchString) {
        return TextUtilities.computeLevenshteinDistance(
            searchString,
            get().toString()
        );
    }

    public V get() {
        if (valueHolder == null) return null;

        return valueHolder.getValue();
    }

    public void set(V v, User user) {
        valueHolder.setValue(v, user);
    }
}
