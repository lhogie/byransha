package byransha;

public class FileNode extends ValuedNode<byte[]> {

    public StringNode title;

    public FileNode (BBGraph g) {
        super(g);
        title = BNode.create(g, StringNode.class);
    }

    public FileNode (BBGraph g, int id) {
        super(g, id);
        //title = BNode.create(g, StringNode.class);
    }

    @Override
    public void fromString(String s) {
        if (s == null || s.isEmpty()) {
            System.err.println("FileNode fromString received null or empty string: " + this);
            return;
        }
        set(s.getBytes());

    }

    @Override
    public String whatIsThis() {
        if(title.get() == null || title.get().isEmpty()) {
            System.err.println("FileNode with no title: " + this);
            return "FileNode(unknown)";
        }
        return "File" + title.get();
    }

    @Override
    public String prettyName() {
        if(title.get() == null || title.get().isEmpty()) {
            System.err.println("FileNode with no title: " + this);
            return "FileNode(unknown)";
        }
        return title.get();
    }

    @Override
    public String getAsString() {
        if (get() == null) {
            System.err.println("FileNode with no value: " + this);
            return "";
        }
        return new String(get());
    }
}
