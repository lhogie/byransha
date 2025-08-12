package byransha;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

public  class Out<V extends BNode> extends ValuedNode<V> {
    public Out(BBGraph db, User user) {
        super(db, user);
    }

    public Out(BBGraph db, int id, User user) {
        super(db, id, user);
    }

    @Override
    protected void saveValue(ValueHistoryEntry<V> e, Consumer<File> writingFiles) {
        //symlink
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
