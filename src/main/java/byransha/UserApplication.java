package byransha;

public abstract class UserApplication extends BNode {
    ImageNode icon = null;
    final BNode rootNode;

    public UserApplication(BBGraph g){
        super(g);
        rootNode = g.create(rootNodeClass());
    }

    protected abstract Class<? extends BNode> rootNodeClass() ;


    public String name(){
        return rootNode.prettyName();
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
