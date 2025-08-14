package byransha;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.function.Consumer;

public  class Out<V extends BNode> extends ValuedNode<V> {
    Field f;

    public Out(BBGraph g, User user) {
        super(g, user, true);
        endOfConstructor();
    }

    public Out(BBGraph g, User user, int id) {
        super(g,  user, id);
        endOfConstructor();
    }

    @Override
    protected void saveValue(ValueHistoryEntry<V> e, Consumer<File> writingFiles) throws IOException {
        var link = new File(e.directory(), "value");
        var target =get().directory().toPath();
        writingFiles.accept(link);
        Files.createSymbolicLink(link.toPath(), target);
    }

    @Override
    public String whatIsThis() {
        return "an arc";
    }

    @Override
    public String prettyName() {
        return "arc: " + history.get();
    }

}
