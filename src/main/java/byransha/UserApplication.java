package byransha;

public abstract class UserApplication extends BNode {
    DocumentNode icon = null;
    final BNode rootNode;

    public UserApplication(BBGraph g, User user){
        super(g, user);
        try {
            rootNode = rootNodeClass().getConstructor(BBGraph.class, User.class)
                    .newInstance(g, user);
        } catch (Throwable err) {
            throw new RuntimeException(err);
        }
        endOfConstructor();
    }

    protected abstract Class<? extends BNode> rootNodeClass() ;


    public String name(){
        return rootNode == null ? null : rootNode.prettyName() == null ? null : rootNode.prettyName();
    }

    @Override
    public String whatIsThis() {
        return "a byransha-based application";
    }

    @Override
    public String prettyName() {
        return name();
    }
}
