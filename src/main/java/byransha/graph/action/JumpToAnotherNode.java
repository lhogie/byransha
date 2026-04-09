package byransha.graph.action;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.NodeAction;
import byransha.nodes.primitive.IDNode;
import byransha.nodes.system.ChatNode;
import byransha.util.Base62;

public class JumpToAnotherNode extends NodeAction<BNode, BNode> {
	final IDNode targetID = new IDNode(g);
	BNode target;

	public JumpToAnotherNode(BGraph g, BNode in) {
		super(g, in, node.class);
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
