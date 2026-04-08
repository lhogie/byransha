package byransha.graph.relection;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.NodeAction;
import byransha.graph.action.ActionResult;
import byransha.nodes.system.ChatNode;

public class MakeNewInstance extends NodeAction<ClassNode, BNode> {

	public MakeNewInstance(BGraph g, ClassNode inputNode) {
		super(g, inputNode, "boh");
	}

	@Override
	public String whatItDoes() {
		return "creates a new instance";
	}

	@Override
	public ActionResult exec(ChatNode chat) throws Throwable {
		return createResultNode(inputNode.newInstance(), readOnly);
	}

	@Override
	public boolean applies(ChatNode chat) {
		return true;
	}

}
