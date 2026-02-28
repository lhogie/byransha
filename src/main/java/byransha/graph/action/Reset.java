package byransha.graph.action;

import byransha.graph.BBGraph;
import byransha.graph.BNode;
import byransha.graph.NodeAction;
import byransha.nodes.primitive.ValuedNode;

final public class Reset extends NodeAction {
	public Reset(BBGraph g, NodeAction action) {
		super(g, action);
	}

	@Override
	public String whatItDoes() {
		return "reset the values";
	}

	@Override
	protected ActionResult exec(BNode target) {
		target.forEachOutField(f -> {
			try {
				var v = (BNode) f.get(target);

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