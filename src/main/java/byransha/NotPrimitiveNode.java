package byransha;

import byransha.labmodel.model.v0.NodeInstantiationParameters;

public abstract class NotPrimitiveNode extends BNode {
    public NotPrimitiveNode(BBGraph g, User creator, InstantiationInfo ii) {
        super(g, creator, ii);
        endOfConstructor();
    }

    @Override
    protected void createOuts(User creator) {
        super.createOuts(creator);
    }
}
