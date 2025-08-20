package byransha;

public abstract class PrimitiveValueNode<V>  extends ValuedNode<V>  {
    protected PrimitiveValueNode(BBGraph db, User user, InstantiationInfo ii) {
        this(db, user, ii, false);
    }

    protected PrimitiveValueNode(BBGraph db, User user, InstantiationInfo ii, boolean historize) {
        super(db, user, ii, historize);
        endOfConstructor();
    }

    public abstract void fromString(String s, User user);


    @Override
    protected void createOuts(User creator) {
    // primitives nodes have no specific outs
    }
}
