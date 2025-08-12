package byransha;

public class EmailNode extends StringNode {

	public EmailNode(BBGraph g, User creator, String s) {
		super(g, creator, s);
	}

	public EmailNode(BBGraph g, User creator) {
		super(g, creator);
	}

	public EmailNode(BBGraph g, User creator, int id) {
		super(g, creator, id);
	}

	public static final String re = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";

}
