package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.ListNode;
import byransha.User;

public class ResearchGroup extends Structure {
    public ListNode<ACMClassifier> keywords;


    public ResearchGroup(BBGraph g, User creator, InstantiationInfo ii) {
        super(g, creator, ii);
        endOfConstructor();
    }

    @Override
    protected void createOuts(User creator) {
        super.createOuts(creator);
        keywords = new ListNode(g, creator, InstantiationInfo.persisting); // new ListNode<>(g);
    }
}
