package byransha;

public abstract class PrimitiveValueNode<V>  extends ValuedNode<V>  {
    protected PrimitiveValueNode(BBGraph db, User user, InstantiationInfo ii) {
        this(db, user, ii, true);
    }

    protected PrimitiveValueNode(BBGraph db, User user, InstantiationInfo ii, boolean historize) {
        super(db, user, ii, historize);
        endOfConstructor();
    }

    public abstract void fromString(String s, User user);


    @Override
    protected void createOuts(User creator) {
        super.createOuts(creator);
        // primitives nodes have no specific outs
    }
}
