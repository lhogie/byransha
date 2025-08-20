package byransha;

public abstract class PrimitiveValueNode<V>  extends ValuedNode<V>  {


    public PrimitiveValueNode(BBGraph db, User user) {
        super(db, user, true);
        endOfConstructor();
    }

    public PrimitiveValueNode(BBGraph db, User user, int id) {
        super(db, user, id);
        endOfConstructor();
    }

    public abstract void fromString(String s, User user);


    @Override
    protected void createOuts(User creator) {
    // primitives nodes have no specific outs
    }
}
