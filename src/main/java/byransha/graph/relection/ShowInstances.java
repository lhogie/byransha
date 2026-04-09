package byransha.graph.relection;

import byransha.graph.BGraph;
import byransha.graph.BNode;
import byransha.graph.NodeAction;
import byransha.graph.action.ActionResult;
import byransha.graph.action.list.ListNode;
import byransha.graph.relection.LinkAction.type;
import byransha.nodes.system.ChatNode;

public class ShowInstances extends NodeAction<ClassNode, ListNode<BNode>> {

	public ShowInstances(BGraph g, ClassNode inputNode) {
		super(g, inputNode, type.class);
	}

	@Override
	public String whatItDoes() {
		return "show instances";
	}

	@Override
	public ActionResult<ClassNode, ListNode<BNode>> exec(ChatNode chat) throws Throwable {
		var r = new ListNode<>(g, "all instances");
		r.elements.addAll(inputNode.allInstances());
		return createResultNode(r, readOnly);
	}

	@Override
	public boolean applies(ChatNode chat) {
		return true;
	}

}
