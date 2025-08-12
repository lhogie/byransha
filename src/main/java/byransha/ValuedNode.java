package byransha;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import toools.text.TextUtilities;

public abstract class ValuedNode<V> extends BNode {
    ValueHistory<V> history;

    public ValuedNode(BBGraph g, User user) {
        super(g, user);
    }

    public ValuedNode(BBGraph g, int id, User user) {
        super(g, user, id);
    }

    public V get(){
        if (history.size() == 0){
            return null;
        }
        else{
            return history.getElements().getLast().value;
//            return history.getAt(graph.date());
        }
    }

    public void set(V value, User user){
        var e = new ValueHistoryEntry<V>(this, value, OffsetDateTime.now());
        history.add(e, user);
    }

    protected abstract void saveValue(ValueHistoryEntry<V> e, Consumer<File> writingFiles);


    @Override
    public void save(Consumer<File> writingFiles) {
        super.save(writingFiles);
    }

    @Override
    public final String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append("(value=\"").append(history.get()).append("\")");
        return sb.toString();
    }



    public String getAsString() {
        V value = get();
        return value != null ? value.toString() : "";
    }

    @Override
    public int distanceToSearchString(String searchString) {
        return TextUtilities.computeLevenshteinDistance(
            searchString,
            get().toString()
        );
    }
}
