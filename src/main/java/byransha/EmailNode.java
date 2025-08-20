package byransha;

public class EmailNode extends StringNode {

	public EmailNode(BBGraph g, User creator, InstantiationInfo ii, String s) {
		super(g, creator, ii, s);
		endOfConstructor();
	}

	public EmailNode(BBGraph g, User creator, InstantiationInfo ii) {
		super(g, creator, ii);
		endOfConstructor();
	}

	public static final String re = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";

}
