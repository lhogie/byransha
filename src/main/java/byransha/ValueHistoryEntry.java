package byransha;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;

public class ValueHistoryEntry<N> extends BNode {
    private N value;
    private DateNode date;
    private User user;
    private ValuedNode<N> vn;

    public ValueHistoryEntry(ValuedNode<N> vn, N value, OffsetDateTime date, User creator, InstantiationInfo ii) throws IOException {
        super(vn.g, vn.g.systemUser(), ii);
        this.vn = vn;
        this.value = value;
        this.date = new DateNode(graph, creator, date);
        this.user = creator;
        save();
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
            this.value = vn.bytesToValue(Files.readAllBytes(valueFile()), g.systemUser());
        } else {
            this.value = null;
        }
    }

    @Override
    public  void save() throws IOException {
        super.save();

        if (value == null) {
            Path filePath = valueFile();

            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
        } else {
            BBGraph.logger.accept(BBGraph.LOGTYPE.FILE_WRITE, valueFile().toString());
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
