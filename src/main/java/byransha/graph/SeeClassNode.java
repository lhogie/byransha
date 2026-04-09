package byransha.graph;

import byransha.graph.action.ActionResult;
import byransha.graph.relection.ClassNode;
import byransha.nodes.system.ChatNode;

public class SeeClassNode extends NodeAction<BNode, ClassNode> {

	public SeeClassNode(BGraph g, BNode inputNode) {
		super(g, inputNode, node.class);
	}

	@Override
	public String whatItDoes() {
		return "show the class node for this node";
	}

	@Override
	public ActionResult exec(ChatNode chat) throws Throwable {
		return createResultNode(inputNode.getClassNode(), true);
	}

	@Override
	public boolean applies(ChatNode chat) {
		return true;
	}

}
