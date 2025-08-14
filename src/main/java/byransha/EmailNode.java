package byransha;

public class EmailNode extends StringNode {

	public EmailNode(BBGraph g, User creator, String s) {
		super(g, creator, s);
		endOfConstructor();
	}

	public EmailNode(BBGraph g, User creator) {
		super(g, creator);
		endOfConstructor();
	}

	public EmailNode(BBGraph g, User creator, int id) {
		super(g, creator, id);
		endOfConstructor();
	}

	public static final String re = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";

}
