package byransha.graph.action;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.NodeAction;
import byransha.graph.action.FreezingAction.misc;
import byransha.nodes.system.ChatNode;

final public class Back extends NodeAction<BNode, BNode> {
	public Back(BGraph g, BNode n) {
		super(g, n, misc.class);
	}

	@Override
	public String whatItDoes() {
		return "back in history";
	}

	@Override
	public ActionResult exec(ChatNode chat) {
		var h = chat.nodes.elements;

		if (applies(chat)) {
			h.remove(h.size() - 1);
			var next = h.remove(h.size() - 1);
			return createResultNode(next, true);
		} else {
			return createResultNode(inputNode, true);
		}
	}

	@Override
	public boolean applies(ChatNode chat) {
		return chat.nodes.elements.size() > 1;
	}

}