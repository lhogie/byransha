package byransha;

public class Cluster extends ListNode<BNode> {

    public Class<? extends BNode> typeOfCluster;

    public Cluster(BBGraph g) {
        super(g);
        //this.setColor("#9900ff");
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
            return "CL " + this.typeOfCluster;
        }
        return "a cluster empty";
    }
}
