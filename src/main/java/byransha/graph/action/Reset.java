package byransha.graph.action;

import byransha.graph.BBGraph;
import byransha.graph.BNode;
import byransha.graph.NodeAction;
import byransha.nodes.primitive.ValuedNode;

final public class Reset extends NodeAction<BNode, BNode> {
	public Reset(BBGraph g, BNode n) {
		super(g, n);
	}

	@Override
	public String whatItDoes() {
		return "reset the values";
	}

	@Override
	public ActionResult exec() {
		inputNode.forEachOutField(f -> {
			try {
				var v = (BNode) f.get(inputNode);

				if (v instanceof ValuedNode vn) {
					vn.set(vn.defaultValue());
				}
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		});

		return null;
	}
}