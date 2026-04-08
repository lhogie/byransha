package byransha.graph.action;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.Hide;
import byransha.graph.NodeAction;
import byransha.nodes.primitive.IDNode;
import byransha.nodes.system.ChatNode;
import byransha.util.Base62;

public class JumpToAnotherNode extends NodeAction<BNode, BNode> {
	final IDNode targetID = new IDNode(g);
	@Hide
	BNode target;

	public JumpToAnotherNode(BGraph g, BNode in) {
		super(g, in, "navigation");
		targetID.valueChangeListeners.add((node, oldV, newV) -> {
			if (targetID.accept(newV)) {
				this.target = g.indexes.byId.get(Base62.decode(newV));
			}
		});
	}

	@Override
	public String toString() {
		return "jump to node " + target;
	}

	@Override
	public String whatItDoes() {
		return "jumps to another node";
	}

	@Override
	public ActionResult<BNode, BNode> exec(ChatNode chat) {
		return createResultNode(target, true);
	}

	@Override
	public boolean applies(ChatNode chat) {
		return true;
	}
}
