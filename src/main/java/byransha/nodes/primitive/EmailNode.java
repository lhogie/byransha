package byransha.nodes.primitive;

import byransha.BBGraph;
import byransha.nodes.system.User;

public class EmailNode extends StringNode {

	public EmailNode(BBGraph g, User creator, String s) {
		super(g, creator, s);
	}

	public static final String re = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";

}
