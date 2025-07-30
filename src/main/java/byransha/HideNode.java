package byransha;

public class HideNode extends ValuedNode<Boolean> {

    public String name = "hide";
    public BNode nodeToSetVisible;

    public HideNode(BBGraph db) {
        super(db);
        set(false);
    }

    public HideNode(BBGraph db, int id) {
        super(db, id);
    }

    @Override
    public String prettyName() {
        return name;
    }

    @Override
    public void fromString(String s) {
        set(Boolean.valueOf(s));
    }

    @Override
    public String whatIsThis() {
        return "a hide node with name : " + name;
    }

    @Override
    public void set(Boolean newValue) {
        super.set(newValue);
        if (nodeToSetVisible != null) {
            nodeToSetVisible.isVisible = newValue;
            if (newValue && nodeToSetVisible instanceof ValuedNode<?>) {
                ((ValuedNode<?>) nodeToSetVisible).set(null);
            }
        }
    }

    public void setNodeToSetVisible(BNode nodeToSetVisible) {
        this.nodeToSetVisible = nodeToSetVisible;
        this.nodeToSetVisible.isVisible = this.get();
    }

    public void setName(String name) {
        this.name = name;
    }
}
