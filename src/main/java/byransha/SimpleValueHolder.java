package byransha;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SimpleValueHolder<N> extends BNode implements ValueHolder<N> {
    private ValuedNode<N> valuedNode;
    private N v;

    public SimpleValueHolder(BBGraph g, User creator, InstantiationInfo ii) {
        super(g, creator, ii);
        endOfConstructor();
    }

    public SimpleValueHolder(ValuedNode<N> vn) {
        super(vn.g, vn.g.systemUser(), InstantiationInfo.persisting);
        this.valuedNode = vn;
        endOfConstructor();
    }

    @Override
    public N getValue() {
        if(v == null){
            try {
                load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return v;
    }

    @Override
    public void setValue(N n, User user) {
        this.v = n;
        try {
            save();
        } catch (IOException e) {
            throw new RuntimeException("Failed to save value", e);
        }
    }

    @Override
    protected void createOuts(User creator) {
        super.createOuts(creator);
    }

    @Override
    public String whatIsThis() {
        return "SimpleValueHolder";
    }

    @Override
    public String prettyName() {
        return null;
    }

    private Path valueFile(){
        return new File(directory(),"value").toPath();
    }

    @Override
    public void save() throws IOException {
        if (valuedNode == null) {
            return;
        }
        super.save();

        g.logger.accept(BBGraph.LOGTYPE.FILE_WRITE, valueFile().toString());
        if (v == null) {
            Path filePath = valueFile();

            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
        } else {
            BBGraph.logger.accept(BBGraph.LOGTYPE.FILE_WRITE, valueFile().toString());
            Files.write(valueFile(), valuedNode.valueToBytes(v));
        }
    }

    public void load() throws IOException {
        if (Files.exists(valueFile())) {
            this.v = valuedNode.bytesToValue(Files.readAllBytes(valueFile()), g.systemUser());
        } else {
            this.v = null;
        }
    }
}
