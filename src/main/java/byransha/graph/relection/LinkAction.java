package byransha.graph.relection;

import byransha.graph.BGraph;
import byransha.graph.Category;
import byransha.graph.NodeAction;
import byransha.graph.action.ActionResult;
import byransha.nodes.system.ChatNode;

public class LinkAction extends NodeAction<ClassNode, ClassNode> {

	public static class type extends Category{} 
	
	public LinkAction(BGraph g, ClassNode inputNode) {
		super(g, inputNode, type.class);
	}

	@Override
	public String whatItDoes() {
		return "link";
	}

	@Override
	public ActionResult exec(ChatNode chat) throws Throwable {
		inputNode.link();
		return createResultNode(null, true);
	}

	@Override
	public boolean applies(ChatNode chat) {
		return true;
	}

}
