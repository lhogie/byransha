package byransha.nodes.lab.model.v0;

import byransha.BBGraph;
import byransha.nodes.primitive.ListNode;
import byransha.nodes.primitive.StringNode;
import byransha.nodes.system.User;
import byransha.annotations.Required;

public class Publication extends BusinessNode {
    @Required
    public StringNode title;

    @Required
    public ListNode<Person> authors;

    public ACMClassifier acmClassifier;

    public Publication(BBGraph g, User creator) {
        super(g, creator);
        this.title = new StringNode(g, creator);
        this.authors = new ListNode<>(g, creator);
        this.acmClassifier = new ACMClassifier(g, creator);
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
        if(title != null && title.get() != null) return title.get();
        return null;
    }
}
