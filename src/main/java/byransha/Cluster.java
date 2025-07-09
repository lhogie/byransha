package byransha;

import byransha.labmodel.model.v0.BusinessNode;

public class Cluster extends SetNode<BNode> {

    public String typeOfCluster;

    public Cluster(BBGraph g) {
        super(g);
        //this.setColor("#9900ff");
    }

    public Cluster(BBGraph g, int id) {
        super(g, id);
    }

    public void setTypeOfCluster(String type) {
        this.typeOfCluster = type;
    }

    public String getTypeOfCluster() {
        return this.typeOfCluster;
    }

    @Override
    public String whatIsThis() {
        return "a cluster to group nodes together";
    }

    @Override
    public String prettyName() {
        if (this.size() > 0) {
            return "a cluster with types " + this.typeOfCluster +  " nodes";
        }
        return "a cluster empty";
    }
}
