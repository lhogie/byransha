package byransha.labmodel.model.v0;

import byransha.BBGraph;
import byransha.ListNode;
import byransha.StringNode;
import byransha.User;

public class Publication extends BusinessNode {

    public StringNode title;
    public ListNode<Person> authors;
    public ACMClassifier acmClassifier;

    public Publication(BBGraph g, StringNode title, ListNode<Person> authors, User creator) {
        super(g, creator);
        this.title = title;
        this.authors = authors;
        endOfConstructor();
    }

    public Publication(BBGraph g, User creator, int id) {
        super(g, creator, id);
        endOfConstructor();
    }

    public Publication(BBGraph g, User creator) {
        super(g, creator);
        this.title = new StringNode(g, creator);
        endOfConstructor();
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
