package byransha;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import toools.text.TextUtilities;

public abstract class ValuedNode<V> extends BNode {

    V value;
    protected String mimeType = "text/plain";

    public ValuedNode(BBGraph db) {
        super(db);
    }

    public ValuedNode(BBGraph db, int id) {
        super(db, id);
    }

    @Override
    public final String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append("(value=\"").append(get()).append("\")");
        return sb.toString();
    }

    //	@Override
    //	public void forEachOut(BiConsumer<String, BNode> consumer) {
    //	}

    public abstract void fromString(String s);

    @Override
    public int distanceToSearchString(String searchString) {
        return TextUtilities.computeLevenshteinDistance(
            searchString,
            get().toString()
        );
    }

    public V get() {
        V localValue = value;
        if (localValue == null) {
            synchronized (this) {
                localValue = value;
                if (localValue == null) {
                    if (directory() != null) {
                        try {
                            loadValue(f -> {});
                            localValue = value;
                        } catch (IOException e) {
                            System.err.println(
                                "Error loading value for node " +
                                id() +
                                ": " +
                                e.getMessage()
                            );
                            throw new RuntimeException(
                                "Failed to load value for node " + id(),
                                e
                            );
                        }
                    }
                }
            }
        }

        return localValue;
    }

    public String getAsString() {
        V value = get();
        return value != null ? value.toString() : "";
    }

    public void set(V newValue) {
        this.value = newValue;
        if (directory() != null) {
            saveValue(BBGraph.sysoutPrinter);
        }
    }

    public void saveValue(Consumer<File> writingFiles) {
        var valueFile = new File(directory(), "value.txt");
        var dir = valueFile.getParentFile();

        if (!dir.exists()) {
            writingFiles.accept(dir);
            dir.mkdirs();
        }

        writingFiles.accept(valueFile);

        try {
            //			System.out.println(graph.findRefsTO(this));
            if (value == null) {
                if (valueFile.exists()) {
                    valueFile.delete();
                }
            } else {
                Files.write(
                    valueFile.toPath(),
                    value.toString().getBytes(StandardCharsets.UTF_8)
                );
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadValue(Consumer<File> readingFiles) throws IOException {
        File valueFile = new File(directory(), "value.txt");

        if (valueFile.exists() && valueFile.isFile()) {
            readingFiles.accept(valueFile);
            byte[] bytes = Files.readAllBytes(valueFile.toPath());
            fromString(new String(bytes, StandardCharsets.UTF_8));
        }
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getMimeType() {
        return mimeType;
    }
}
