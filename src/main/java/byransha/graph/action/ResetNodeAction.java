package byransha.graph.action;

import byransha.graph.BBGraph;
import byransha.graph.BNode;
import byransha.graph.NodeAction;
import byransha.nodes.primitive.ValuedNode;

final public class ResetNodeAction extends NodeAction {
	public ResetNodeAction(BBGraph g) {
		super(g);
	}

	@Override
	public String whatItDoes() {
		return "reset the values";
	}

	@Override
	public ActionResult exec(BNode target) {
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