package byransha.nodes.primitive;

import java.util.List;

import byransha.graph.BBGraph;
import byransha.graph.NodeError;
import byransha.nodes.system.User;

public class EmailNode extends StringNode {

	public static final String re = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";

	public EmailNode(BBGraph g, User creator, String s) {
		super(g, creator, s, re);
	}

	@Override
	protected void fillErrors(List<NodeError> errs, int depth) {
		if (!get().matches(re)) {
			errs.add(new NodeError(this, "invalid email address"));
		}
	}
}
