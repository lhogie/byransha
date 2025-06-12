package byransha;

public class EmailNode extends StringNode {

	public EmailNode(BBGraph g, String s) {
		super(g, s);
	}

	public EmailNode(BBGraph g, int id) {
		super(g, id);
	}

	public EmailNode(BBGraph g) {
		super(g, null);
	}

	public static final String re = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";

}
