package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.ListNode;
import byransha.StringNode;
import byransha.User;
import byransha.web.NodeEndpoint;

public class Publication extends BusinessNode {
    public StringNode title;
    public ListNode<Person> authors;
    public ACMClassifier acmClassifier;

    public Publication(BBGraph g, User creator, InstantiationInfo ii) {
        super(g, creator, ii);
        endOfConstructor();
    }

    @Override
    protected void createOuts(User creator) {
        super.createOuts(creator);
        this.title = new StringNode(g, creator, InstantiationInfo.persisting);
        this.authors = new ListNode<>(g, creator, InstantiationInfo.persisting);
        this.acmClassifier = new ACMClassifier(g, creator, InstantiationInfo.persisting);
    }

    @Override
    public String whatIsThis() {
        return "Publication: " + title.get();
    }

    @Override
    public String toString() {
        if (title == null) {
            return "Publication: " + id();
        }
        return title.get();
    }

    @Override
    public String prettyName() {
        return "publication " + title.get();
    }
}
