package byransha.graph.action;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.Hide;
import byransha.graph.NodeAction;
import byransha.nodes.primitive.LongNode;
import byransha.nodes.system.ChatNode;

public class Jump extends NodeAction<BNode, BNode> {
	final LongNode targetID;
	@Hide
	BNode target;

	public Jump(BGraph g, BNode in) {
		super(g, in);
		targetID = new LongNode(g);

		targetID.changeListeners.add(l -> {
			var node = g.indexes.byId.get(targetID.get());
			target = node;
		});

		target = g;
	}

	@Override
	public String prettyName() {
		return "jump to node " + target;
	}

	@Override
	public String whatItDoes() {
		return "jumps to another node";
	}

	@Override
	public ActionResult<BNode, BNode> exec(ChatNode chat) {
		chat.append(target);
		return createResultNode(target, true);
	}

	@Override
	public boolean applies(ChatNode chat) {
		return true;
	}
}
