package byransha;

public class Cluster extends ListNode<BNode> {

    public Class<? extends BNode> typeOfCluster;

    public Cluster(BBGraph g, User creator, InstantiationInfo ii) {
        super(g, creator, ii, false);
        endOfConstructor();
    }

    public void setTypeOfCluster(Class<? extends BNode> type) {
        this.typeOfCluster = type;
    }

    public Class<? extends BNode> getTypeOfCluster() {
        return this.typeOfCluster;
    }

    @Override
    public String whatIsThis() {
        return "a cluster to group nodes together";
    }

    @Override
    public String prettyName() {
        if (this.size() > 0) {
            return this.typeOfCluster.getSimpleName();
        }
        return "a cluster empty";
    }
}
