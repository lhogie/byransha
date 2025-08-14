package byransha;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.function.Consumer;

public abstract class PrimitiveValueNode<V>  extends ValuedNode<V> {

    protected String mimeType = "text/plain";


    public PrimitiveValueNode(BBGraph db, User user) {
        super(db, user);
    }

    public PrimitiveValueNode(BBGraph db, User user, int id) {
        super(db, id, user);
    }

    public abstract void fromString(String s, User user);

    protected byte[] toBytes(V v) throws IOException {
        return v == null
                ? new byte[0]
                : v.toString().getBytes(StandardCharsets.UTF_8);
    }


    protected void fromBytes(byte[] bytes, User user) throws IOException {
        if (bytes == null) {
            fromString("", user);
        } else {
            fromString(new String(bytes, StandardCharsets.UTF_8), user);
        }
    }

    private File valueFile(ValueHistoryEntry<V> e) {
        return new File(e.directory(), "value.txt");
    }

    @Override
    public void saveValue(ValueHistoryEntry<V> e, Consumer<File> writingFiles) {
        File valueFile = valueFile(e);
        var dir = valueFile.getParentFile();

        if (!dir.exists()) {
            writingFiles.accept(dir);
            dir.mkdirs();
        }

        writingFiles.accept(valueFile);
        var value = get();

        try {
            //			System.out.println(graph.findRefsTO(this));
            if (value == null) {
                if (valueFile.exists()) {
                    valueFile.delete();
                }
            } else {
                Files.write(valueFile.toPath(), toBytes(value));
            }
        } catch (IOException err) {
            throw new RuntimeException(err);
        }
    }



    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getMimeType() {
        return mimeType;
    }
}
