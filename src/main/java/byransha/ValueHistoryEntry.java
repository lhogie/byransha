package byransha;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.function.Consumer;

public class ValueHistoryEntry<N> extends BNode {
    private N value;
    private DateNode date;
    private User user;
    private ValuedNode<N> vn;

    public ValueHistoryEntry(ValuedNode<N> vn, N value, OffsetDateTime date, User creator) throws IOException {
        super(vn.graph, vn.graph.systemUser());
        this.vn = vn;
        this.value = value;
        this.date = new DateNode(graph, creator, date);
        this.user = creator;
        save(BBGraph.sysoutPrinter);
        endOfConstructor();
    }

    public ValueHistoryEntry(BBGraph g, User creator, int id) throws IOException {
        super(g, g.systemUser(), id);
        endOfConstructor();
    }

    private Path valueFile(){
        return new File(directory(),"value").toPath();
    }

    public void load() throws IOException {
        if (Files.exists(valueFile())) {
            this.value = vn.bytesToValue(Files.readAllBytes(valueFile()), graph.systemUser());
        } else {
            this.value = null;
        }
    }

    @Override
    public  void save(Consumer<File> writingFiles) throws IOException {
        super.save(writingFiles);

        if (value == null) {
            Path filePath = valueFile();

            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
        } else {
            writingFiles.accept(valueFile().toFile());
            Files.write(valueFile(), vn.valueToBytes(value));
        }
    }

    @Override
    public String whatIsThis() {
        return "an element of history for a given node";
    }

    @Override
    public String prettyName() {
        return value.toString();
    }

    @Override
    public boolean canEdit(User user) {
        return false;
    }

    public N value() {
        if( value == null ){
            try {
                load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return value;
    }
}
