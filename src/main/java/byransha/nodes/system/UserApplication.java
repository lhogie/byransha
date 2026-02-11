package byransha.nodes.system;

import byransha.BBGraph;
import byransha.nodes.DocumentNode;
import byransha.nodes.BNode;

public abstract class UserApplication extends BNode {
    DocumentNode icon = null;
    final BNode rootNode;

    public UserApplication(BBGraph g, User user){
        super(g, user);

        try {
            this.rootNode = rootNodeClass().getConstructor(BBGraph.class, User.class)
                    .newInstance(g, user);
        } catch (Throwable err) {
            throw new RuntimeException(err);
        }
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
