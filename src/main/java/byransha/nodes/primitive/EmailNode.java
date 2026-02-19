package byransha.nodes.primitive;

import java.util.List;

import byransha.graph.BBGraph;
import byransha.graph.NodeError;

public class EmailNode extends StringNode {

	public static final String re = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";

	public EmailNode(BBGraph g, String s) {
		super(g, s, re);
	}

	@Override
	protected void fillErrors(List<NodeError> errs) {
		if (!get().matches(re)) {
			errs.add(new NodeError(this, "invalid email address"));
		}
	}
}
