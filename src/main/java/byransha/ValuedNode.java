package byransha;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;
import toools.text.TextUtilities;

public abstract class ValuedNode<V> extends BNode  {
    ValueHolder<V> valueHolder;

    public ValuedNode(BBGraph g, User user, boolean historize) {
        super(g, user);
        this.valueHolder = historize ? new ValueHistory<V>(this) : new SimpleValueHolder<>();
        endOfConstructor();
    }

    public ValuedNode(BBGraph g, User user, int id) {
        super(g, user, id);
        endOfConstructor();
    }

    protected abstract byte[] valueToBytes(V v) throws IOException;

    protected abstract V bytesToValue(byte[] bytes, User user) throws IOException;

    @Override
    public void save(Consumer<File> writingFiles) throws IOException {
        super.save(writingFiles);
    }

    @Override
    public final String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append("(value=\"").append(get()).append("\")");
        return sb.toString();
    }

    public String getAsString() {
        V v = get();
        return v != null ? v.toString() : "";
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
